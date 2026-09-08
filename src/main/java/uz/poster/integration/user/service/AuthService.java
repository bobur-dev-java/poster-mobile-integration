package uz.poster.integration.user.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import uz.poster.integration.user.dto.AuthResponseDto;
import uz.poster.integration.notification.otp.dto.SendOtpDto;
import uz.poster.integration.notification.otp.dto.VerifyOtpDto;

import java.util.Map;

@Service
public interface AuthService {
    Map<String, Object> sendOtp(SendOtpDto dto);

    AuthResponseDto verifyOtp(String userAgent, HttpServletRequest request, @Valid VerifyOtpDto dto);

    AuthResponseDto refreshToken(@NotBlank String refreshToken);
}
