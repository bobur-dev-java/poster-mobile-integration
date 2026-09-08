package uz.poster.integration.notification.otp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpDto {
    @NotBlank
    @Pattern(regexp = "^\\+998\\d{9}$", message = "Phone number must be in format +998XXXXXXXXX")
    private String phone;

    @NotBlank
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;

    private String deviceId;
    private String deviceName;
    private String platform;
}
