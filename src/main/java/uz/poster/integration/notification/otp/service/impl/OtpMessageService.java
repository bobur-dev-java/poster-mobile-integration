package uz.poster.integration.notification.otp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.poster.integration.notification.otp.dto.SmsStatusRequest;
import uz.poster.integration.notification.otp.entity.OtpMessage;
import uz.poster.integration.notification.otp.repository.OtpMessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpMessageService {
    private final OtpMessageRepository otpMessageRepository;

    @Async
    public void createOtpMessage(String phone, String messageId, String platform) {
        OtpMessage otpMessage = new OtpMessage();
        otpMessage.setMessageId(messageId);
        otpMessage.setPhone(phone);
        otpMessage.setPlatform(platform);
        otpMessage.setStatus("PENDING");
        otpMessageRepository.save(otpMessage);
    }

    @Transactional
    public void updateOtpMessage(SmsStatusRequest request) {
        List<String> messageIds = new ArrayList<>();
        request.getMessages().forEach(message -> {
            messageIds.add(message.getMessageId());
        });

        List<OtpMessage> otpMessageList = otpMessageRepository.findOtpMessagesByMessageIds(messageIds);
        log.info(otpMessageList.toString());

        Map<String, OtpMessage> otpMessageMap = otpMessageList.stream()
                .collect(Collectors.toMap(
                        OtpMessage::getMessageId,
                        Function.identity()
                ));

        for (SmsStatusRequest.MessageStatus status : request.getMessages()) {

            OtpMessage otpMessage = otpMessageMap.get(status.getMessageId());

            if (otpMessage == null) {
                continue;
            }

            otpMessage.setDescription(status.getDescription() != null ? status.getDescription() : null);
            otpMessage.setStatus(status.getStatus() != null ? status.getStatus() : null);
            otpMessage.setChannel(status.getChannel() != null ? status.getChannel() : null);
            otpMessage.setStatusDate(status.getStatusDate() != null ? status.getStatusDate() : null);

            log.info("Message {} has been updated", status.getMessageId());
        }
        log.info("Saving {} entities", otpMessageMap.size());
        otpMessageRepository.saveAll(otpMessageMap.values());

    }
}
