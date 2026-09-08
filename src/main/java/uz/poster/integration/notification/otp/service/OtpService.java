package uz.poster.integration.notification.otp.service;

import org.springframework.stereotype.Service;
import uz.poster.integration.user.entity.enums.Platform;

@Service
public interface OtpService {
    String sendOtp(String phone, Platform platform);

    boolean verifyOtp(String phone, String otp);
}
