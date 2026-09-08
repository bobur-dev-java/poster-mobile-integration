package uz.poster.integration.user.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.poster.integration.common.entity.AuditableEntity;
import uz.poster.integration.user.entity.enums.SessionStatus;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "user_session",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_session_user_device", columnNames = {"user_id", "device_id"})
        },
        indexes = {
                @Index(name = "idx_session_user", columnList = "user_id"),
                @Index(name = "idx_session_status", columnList = "status"),
                // expiresAt bo'yicha ham index — cron/cleanup job muddati o'tgan
                // sessionlarni tez topishi uchun
                @Index(name = "idx_session_expires_at", columnList = "expires_at")
        }
)
public class UserSession extends AuditableEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private String userId;

    @Column(name = "refresh_token_hash", length = 64)
    private String refreshTokenHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "device_id", nullable = false, updatable = false, length = 128)
    private String deviceId;

    @Column(name = "device_name", length = 128)
    private String deviceName;

    @Column(length = 32)
    private String platform;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "last_access_at")
    private Instant lastAccessAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;


    @Transient
    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    @Transient
    public boolean isActive() {
        return status == SessionStatus.ACTIVE && !isExpired();
    }

    public void revoke() {
        this.status = SessionStatus.REVOKED;
        this.revokedAt = Instant.now();
    }

    public void markExpired() {
        this.status = SessionStatus.EXPIRED;
    }
}
