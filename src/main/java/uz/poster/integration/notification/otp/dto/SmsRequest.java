package uz.poster.integration.notification.otp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SmsRequest {
    private Sms sms;
    private List<Message> messages;

    @Data
    public static class Sms {
        private String originator;
        private Content content;
    }

    @Data
    public static class Content {
        private String text;
    }

    @Data
    public static class Message {
        private String recipient;
        @JsonProperty("message-id")
        private String messageId;

    }

}
