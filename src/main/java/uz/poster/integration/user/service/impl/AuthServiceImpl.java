package uz.poster.integration.user.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uz.poster.integration.common.exception.BadRequestException;
import uz.poster.integration.common.exception.NotFoundException;
import uz.poster.integration.common.exception.UnauthorizedException;
import uz.poster.integration.config.properties.JwtProperties;
import uz.poster.integration.notification.otp.service.OtpService;
import uz.poster.integration.security.JwtTokenProvider;
import uz.poster.integration.user.dto.AuthResponseDto;
import uz.poster.integration.user.dto.AuthUserDto;
import uz.poster.integration.notification.otp.dto.SendOtpDto;
import uz.poster.integration.notification.otp.dto.VerifyOtpDto;
import uz.poster.integration.user.entity.User;
import uz.poster.integration.user.entity.UserSession;
import uz.poster.integration.user.entity.enums.SessionStatus;
import uz.poster.integration.user.entity.enums.UserRole;
import uz.poster.integration.user.repository.UserRepository;
import uz.poster.integration.user.repository.UserSessionRepository;
import uz.poster.integration.user.service.AuthService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserSessionRepository userSessionRepository;
    private final JwtProperties jwtProperties;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public Map<String, Object> sendOtp(SendOtpDto dto) {
        String otp = otpService.sendOtp(dto.getPhone(),dto.getPlatform());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "OTP sent successfully");
        if (!"prod".equals(activeProfile)) {
            response.put("otp", otp);
        }
        return response;
    }

    @Transactional
    public AuthResponseDto verifyOtp(String userAgent, HttpServletRequest request, VerifyOtpDto dto) {
        boolean valid = otpService.verifyOtp(dto.getPhone(), dto.getOtp());
        if (!valid) {
            throw new BadRequestException("Invalid or expired OTP");
        }

        User user = userRepository.findByPhone(dto.getPhone())
                .orElseGet(() -> createNewUser(dto));

        if (!user.isVerified()) {
            user.setVerified(true);
            user.setPhoneVerified(true);
            user.setVerifiedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        UserSession session = resolveSession(request, userAgent, user, dto);

        return buildAuthResponse(user, session);
    }


    private UserSession resolveSession(HttpServletRequest request, String userAgent, User user, VerifyOtpDto dto) {
        UserSession session = userSessionRepository
                .findSessionByUserIdAndDeviceId(user.getId(), dto.getDeviceId())
                .orElse(null);

        if (session != null && session.isExpired()) {
            session.markExpired();
            userSessionRepository.save(session);
            session = null;
        } else if (session != null && session.getStatus() != SessionStatus.ACTIVE) {
            session = null;
        }

        if (session != null) {
            return session;
        }

        try {
            return createNewSession(request, userAgent, user, dto);
        } catch (DataIntegrityViolationException e) {
            log.debug("Concurrent session creation detected for user={}, device={}",
                    user.getId(), dto.getDeviceId());
            return userSessionRepository
                    .findSessionByUserIdAndDeviceId(user.getId(), dto.getDeviceId())
                    .orElseThrow(() -> e);
        }
    }

    private User createNewUser(VerifyOtpDto dto) {
        User newUser = new User();
        newUser.setPhone(dto.getPhone());
        newUser.setRole(UserRole.USER);
        newUser.setVerified(true);
        newUser.setPhoneVerified(true);
        newUser.setVerifiedAt(LocalDateTime.now());
        return userRepository.save(newUser);
    }

    private UserSession createNewSession(HttpServletRequest request, String userAgent, User user, VerifyOtpDto dto) {
        Instant now = Instant.now();
        Duration refreshTokenTtl = Duration.ofSeconds(jwtProperties.getRefreshTokenExpiry());

        UserSession newSession = UserSession.builder()
                .user(user)
                .userId(user.getId())
                .deviceName(dto.getDeviceName())
                .platform(dto.getPlatform())
                .deviceId(dto.getDeviceId())
                .ipAddress(extractClientIp(request))
                .userAgent(userAgent)
                .status(SessionStatus.ACTIVE)
                .expiresAt(now.plus(refreshTokenTtl))
                .build();
        return userSessionRepository.save(newSession);
    }

    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank()) {
            return request.getRemoteAddr();
        }
        return ip.split(",")[0].trim();
    }

    @Transactional
    public AuthResponseDto refreshToken(String refreshToken) {
        String userId;
        String sessionId;
        try {
            userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
            sessionId = jwtTokenProvider.getSessionIdFromToken(refreshToken);
        } catch (Exception e) {
            log.debug("Invalid refresh token", e);
            throw new UnauthorizedException("Invalid refresh token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        UserSession session = userSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new NotFoundException("Session not found"));

        if (!session.isActive()) {
            throw new UnauthorizedException("Session expired or revoked");
        }

        String incomingHash = "DigestUtils.sha256Hex(refreshToken)";
        String storedHash = session.getRefreshTokenHash();

        if (storedHash == null || !MessageDigest.isEqual(
                incomingHash.getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8))) {

            // Hash mos kelmasa - token o'g'irlangan yoki eski (allaqachon
            // almashtirilgan) token qayta ishlatilyapti degani. Ehtiyot
            // shart sifatida butun sessionni revoke qilamiz.

            session.revoke();
            userSessionRepository.save(session);
            log.warn("Refresh token reuse/mismatch detected, session revoked: sessionId={}", sessionId);
            throw new UnauthorizedException("Invalid refresh token");
        }

        return buildAuthResponse(user, session);
    }

    private AuthResponseDto buildAuthResponse(User user, UserSession session) {
        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getPhone(), user.getRole(), session.getId());

        String refreshToken = jwtTokenProvider.generateRefreshToken(
                user.getId(), user.getPhone(), user.getRole(), session.getId());

//        session.setRefreshTokenHash(DigestUtils.sha256Hex(refreshToken));
        session.setLastAccessAt(Instant.now());
        userSessionRepository.save(session);

        AuthUserDto userDto = mapToAuthUserDto(user);

        return new AuthResponseDto(accessToken, refreshToken);
    }

    private AuthUserDto mapToAuthUserDto(User user) {
        return AuthUserDto.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .phoneVerified(user.isPhoneVerified())
                .verifiedAt(user.getVerifiedAt())
                .build();
    }
}
