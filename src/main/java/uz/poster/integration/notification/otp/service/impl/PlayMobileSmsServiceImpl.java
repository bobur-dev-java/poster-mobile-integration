package uz.poster.integration.notification.otp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import uz.poster.integration.config.properties.PlayMobileProperties;
import uz.poster.integration.notification.otp.dto.SmsRequest;
import uz.poster.integration.notification.otp.service.SmsService;
import uz.poster.integration.user.entity.enums.Platform;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayMobileSmsServiceImpl implements SmsService {

    private final RestClient restClient;
    private final PlayMobileProperties playMobileProperties;
    private final OtpMessageService otpMessageService;
    private static final String APP_HASH_VALUE = "AbCdEfGhIjK";

    @Override
    public void sendOtp(String phone, String otp, Platform platform) {
        Objects.requireNonNull(phone, "Phone is null");
        Objects.requireNonNull(otp, "Otp is null");
        Objects.requireNonNull(platform, "Platform is null");

        SmsRequest.Sms sms = getSmsFormat(otp, platform);

        SmsRequest.Message message = new SmsRequest.Message();
        message.setRecipient(phone);

        String messageId = UUID.randomUUID().toString();
        message.setMessageId(messageId);

        otpMessageService.createOtpMessage(
                phone,
                messageId,
                platform.name()
        );

        SmsRequest request = new SmsRequest();
        request.setSms(sms);
        request.setMessages(List.of(message));

        ResponseEntity<String> response = restClient.post()
                .uri(playMobileProperties.getUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.setBasicAuth(
                        playMobileProperties.getUsername(),
                        playMobileProperties.getPassword()
                ))
                .body(request)
                .retrieve()
                .toEntity(String.class);

        log.info(
                "OTP for {}: Response: {}",
                phone,
                response.getBody()
        );
    }

    private SmsRequest.@NonNull Sms getSmsFormat(String otp, Platform platform) {
        String txt;
        if (platform == Platform.ANDROID) {
            txt = String.format("<#> BazaUZ ilovasiga kirish kodi:  %s %s", otp, APP_HASH_VALUE);
        } else if (platform == Platform.IOS) {
            txt = String.format("BazaUZ ilovasiga kirish kodi: %s", otp);
        } else {
            throw new IllegalArgumentException("Platform is unknown");
        }

        SmsRequest.Content content = new SmsRequest.Content();
        content.setText(txt);

        SmsRequest.Sms sms = new SmsRequest.Sms();
        sms.setOriginator(playMobileProperties.getOriginator());
        sms.setContent(content);
        return sms;
    }
}
