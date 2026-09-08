package uz.poster.integration.notification.otp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.poster.integration.common.entity.AuditableEntity;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(
        name = "otp_message",
        uniqueConstraints = {
                @UniqueConstraint(name = "users_message_id_key", columnNames = "message_id")
        },
        indexes = {
                @Index(name = "message_id_idx", columnList = "message_id")
        }
)
public class OtpMessage extends AuditableEntity {

    @Column(name = "message_id")
    private String messageId;
    private String phone;
    private String platform;
    private String channel;
    private String status;
    private String statusDate;
    private String description;
}
