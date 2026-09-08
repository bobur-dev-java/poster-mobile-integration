package uz.poster.integration.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.poster.integration.common.entity.AuditableEntity;
import uz.poster.integration.user.entity.enums.UserRole;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "users_phone_key", columnNames = "phone")
        },
        indexes = {
                @Index(name = "users_phoneVerified_idx", columnList = "phone_verified"),
                @Index(name = "users_role_idx", columnList = "role"),
                @Index(name = "users_bannedAt_idx", columnList = "banned_at")
        }
)
public class User extends AuditableEntity {
    @Column(name = "phone", nullable = false)
    private String phone;

    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "role")
    private UserRole role = UserRole.USER;

    @Column(nullable = false)
    private boolean verified = false;

    @Column(nullable = false, name = "phone_verified")
    private boolean phoneVerified = true;

    @Column(columnDefinition = "timestamp(3)")
    private LocalDateTime verifiedAt;

    @Column(columnDefinition = "timestamp(3)", name = "banned_at")
    private LocalDateTime bannedAt;

    @Column(columnDefinition = "text")
    private String banReason;
}
