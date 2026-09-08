package uz.poster.integration.notification.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SmsStatusRequest implements Serializable {
    private List<MessageStatus> messages;

    @Data
    public static class MessageStatus {

        @JsonProperty("message-id")
        private String messageId;
        private String channel;
        private String status;
        @JsonProperty("status-date")
        private String statusDate;
        private String description;

    }
}