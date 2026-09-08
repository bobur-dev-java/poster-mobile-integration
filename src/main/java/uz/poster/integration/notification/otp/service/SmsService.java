package uz.poster.integration.notification.otp.service;


import org.springframework.stereotype.Service;
import uz.poster.integration.user.entity.enums.Platform;
@Service
public interface SmsService {
    void sendOtp(String phone, String otp, Platform platform);
}
