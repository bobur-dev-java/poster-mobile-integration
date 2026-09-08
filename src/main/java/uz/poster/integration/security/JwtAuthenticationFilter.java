package uz.poster.integration.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.poster.integration.common.exception.NotFoundException;
import uz.poster.integration.common.exception.UnauthorizedException;
import uz.poster.integration.user.entity.UserSession;
import uz.poster.integration.user.entity.enums.SessionStatus;
import uz.poster.integration.user.repository.UserSessionRepository;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserSessionRepository sessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authenticate(token, request);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
        } catch (NotFoundException | UnauthorizedException e) {
            log.debug("Session validation failed: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private void authenticate(String token, HttpServletRequest request) {
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        if (userId == null) {
            return;
        }

        String sessionId = jwtTokenProvider.getSessionIdFromToken(token);
        if (sessionId == null) {
            throw new UnauthorizedException("Token missing session");
        }
        validateSession(userId, sessionId);

        UserDetails userDetails = userDetailsService.loadUserById(userId);
        if (!userDetails.isAccountNonLocked()) {
            log.warn("Authentication rejected. Locked account. userId={}", userId);
            return;
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private void validateSession(String userId, String sessionId) {
        // redis ga saqlab shundan olishim ham mumkin

        UserSession session = sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new NotFoundException("Session not found"));

        boolean valid = session.getStatus() == SessionStatus.ACTIVE
                && session.getExpiresAt().isAfter(Instant.now());

        if (!valid) {
            log.warn("Session rejected. Locked account. userId={}, sessionId={}", userId, sessionId);
            throw new UnauthorizedException("Session revoked or expired");
        }
    }
}
