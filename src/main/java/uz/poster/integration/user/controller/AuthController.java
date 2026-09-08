package uz.poster.integration.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uz.poster.integration.common.dto.BaseResponse;
import uz.poster.integration.user.dto.AuthResponseDto;
import uz.poster.integration.user.dto.RefreshTokenDto;
import uz.poster.integration.notification.otp.dto.SendOtpDto;
import uz.poster.integration.notification.otp.dto.VerifyOtpDto;
import uz.poster.integration.user.service.AuthService;

import java.util.Map;

@Tag(name = "auth")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Send OTP to phone number")
    @ApiResponse(responseCode = "200", description = "OTP sent successfully")
    @ApiResponse(responseCode = "400", description = "Invalid phone number")
    public BaseResponse<Map<String, Object>> sendOtp(@Valid @RequestBody SendOtpDto dto) {
        return BaseResponse.success(authService.sendOtp(dto));
    }

    @PostMapping("/verify-otp")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Verify OTP and login/register")
    @ApiResponse(responseCode = "200", description = "OTP verified, tokens returned")
    @ApiResponse(responseCode = "400", description = "Invalid or expired OTP")
    public BaseResponse<AuthResponseDto> verifyOtp(
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            HttpServletRequest request,
            @Valid @RequestBody VerifyOtpDto dto) {
        return BaseResponse.success(authService.verifyOtp(userAgent, request, dto));
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Refresh access token using refresh token")
    @ApiResponse(responseCode = "200", description = "Token refreshed")
    @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    public BaseResponse<AuthResponseDto> refreshToken(@Valid @RequestBody RefreshTokenDto dto) {
        return BaseResponse.success(authService.refreshToken(dto.getRefreshToken()));
    }
}
