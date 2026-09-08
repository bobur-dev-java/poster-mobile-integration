package uz.poster.integration.notification.otp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.poster.integration.user.entity.enums.Platform;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpDto {
    @NotBlank
    @Pattern(regexp = "^\\+998\\d{9}$", message = "Phone number must be in format +998XXXXXXXXX")
    private String phone;
    @NotNull(message = "Platform must not be null")
    private Platform platform;
}
