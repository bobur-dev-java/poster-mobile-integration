package uz.poster.integration.user.dto;

import lombok.*;
import uz.poster.integration.user.entity.enums.UserRole;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthUserDto {
    private String id;
    private String phone;
    private String firstName;
    private String lastName;
    private UserRole role;
    private Boolean phoneVerified;
    private LocalDateTime verifiedAt;
}
