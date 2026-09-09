package uz.poster.integration.notification.otp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import uz.poster.integration.notification.otp.service.OtpService;
import uz.poster.integration.user.entity.enums.Platform;
import uz.poster.integration.notification.otp.service.SmsService;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {
    private final StringRedisTemplate redisTemplate;
    private final SmsService smsService;

    private static final String OTP_KEY_PREFIX = "otp:";
    private static final String ATTEMPTS_KEY_PREFIX = "otp:attempts:";
    private static final long OTP_EXPIRY_MINUTES = 2L;
    private static final int MAX_ATTEMPTS = 3;
    private static final String TEST_PHONE = "+998901234567";

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    public String sendOtp(String phone, Platform platform) {
        String otp = generateOtp();
        String otpKey = OTP_KEY_PREFIX + phone;
        String attemptsKey = ATTEMPTS_KEY_PREFIX + phone;

        redisTemplate.opsForValue().set(otpKey, otp, OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(attemptsKey, "0", OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);

        log.info("OTP for {}: {} (expires in {} minutes)", phone, otp, OTP_EXPIRY_MINUTES);
        // TODO: In production, integrate with SMS provider (Eskiz.uz, Playmobile, etc.)
        if ("!prod".equals(activeProfile))
            smsService.sendOtp(phone, otp, platform);
        return otp;
    }

    public boolean verifyOtp(String phone, String otp) {
        if (!"prod".equals(activeProfile) && TEST_PHONE.equals(phone)) {
            return true;
        }

        String otpKey = OTP_KEY_PREFIX + phone;
        String attemptsKey = ATTEMPTS_KEY_PREFIX + phone;

        String storedOtp = redisTemplate.opsForValue().get(otpKey);
        if (storedOtp == null) {
            log.info("Otp is null for phone: {}", phone);
            return false;
        }

        String attemptsStr = redisTemplate.opsForValue().get(attemptsKey);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;

        if (attempts >= MAX_ATTEMPTS) {
            redisTemplate.delete(otpKey);
            redisTemplate.delete(attemptsKey);
            log.info("Otp attempts is out for phone: {}", phone);
            return false;
        }

        if (storedOtp.equals(otp)) {
            redisTemplate.delete(otpKey);
            redisTemplate.delete(attemptsKey);
            return true;
        }

        redisTemplate.opsForValue().set(attemptsKey, String.valueOf(attempts + 1),
                OTP_EXPIRY_MINUTES, TimeUnit.MINUTES);
        return false;
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(900000) + 100000);
    }
}
