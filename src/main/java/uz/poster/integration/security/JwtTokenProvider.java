package uz.poster.integration.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.poster.integration.config.properties.JwtProperties;
import uz.poster.integration.user.entity.enums.UserRole;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String userId, String phone, UserRole role, String sessionId) {
        return buildToken(userId, phone, role, sessionId, jwtProperties.getAccessTokenExpiry(), "access");
    }

    public String generateRefreshToken(String userId, String phone, UserRole role, String sessionId) {
        return buildToken(userId, phone, role, sessionId, jwtProperties.getRefreshTokenExpiry(), "refresh");
    }

    private String buildToken(String userId, String phone, UserRole role, String sessionId, long expiry, String type) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expiry);
        return Jwts.builder()
                .subject(userId)
                .claim("sid", sessionId)
                .claim("phone", phone)
                .claim("role", role.name())
                .claim("type", type)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserIdFromToken(String token) {
        return validateToken(token).getSubject();
    }

    public String getRoleFromToken(String token) {
        return validateToken(token).get("role", String.class);
    }

    public String getSessionIdFromToken(String refreshToken) {
        return validateToken(refreshToken).get("sid", String.class);
    }
}
