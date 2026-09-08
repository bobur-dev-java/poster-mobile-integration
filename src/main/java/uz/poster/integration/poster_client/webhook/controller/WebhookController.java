package uz.poster.integration.poster_client.webhook.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;
import uz.poster.integration.config.properties.PosterProperties;
import uz.poster.integration.poster_client.service.order.OrderReconciliationService;
import uz.poster.integration.poster_client.util.WebhookSignatureVerifier;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final PosterProperties posterProperties;
    private final OrderReconciliationService orderReconciliationService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @PostMapping
    public ResponseEntity<Map<String, String>> handleWebhook(@RequestBody JsonNode payload) {

        if (!WebhookSignatureVerifier.isValid(payload, posterProperties.applicationSecret())) {
            log.warn("Poster webhook: imzo (verify) mos kelmadi, payload rad etildi: {}", payload);
            // Baribir 200 qaytarish tavsiya etiladi - aks holda Poster qayta-qayta urinaveradi;
            // signature xato bo'lsa bu haqiqiy Poster'dan kelmagan yoki secret noto'g'ri sozlangan degani.
            return ResponseEntity.status(401).body(Map.of("status", "invalid_signature"));
        }

        String object = payload.path("object").asText("");
        String action = payload.path("action").asText("");
        long objectId = payload.path("object_id").asLong();

        log.info("Poster webhook keldi: object={}, action={}, object_id={}", object, action, objectId);

        try {
            switch (object) {
                case "incoming_order" -> handleIncomingOrderChanged(action, objectId);
                case "transaction" -> handleTransactionChanged(action, objectId);
                case "application" -> handleApplicationEvent(action, payload.path("data"));
                default -> log.debug("Bu entity turi ('{}') hozircha ishlanmaydi, e'tiborsiz qoldirildi", object);
            }
        } catch (Exception e) {
            // Xatolik bo'lsa ham 200 qaytaramiz - aks holda Poster shu webhook'ni 15 marta qayta yuboradi.
            // Xatoni albatta logga/monitoringga yozib qo'yish kerak.
            log.error("Webhook'ni qayta ishlashda xatolik: object={}, object_id={}", object, objectId, e);
        }

        // Poster docs'dagi PHP misoliga mos javob - shart emas, lekin mos keladi
        return ResponseEntity.ok(Map.of("status", "accept"));
    }

    private void handleIncomingOrderChanged(String action, long posterIncomingOrderId) {
        if (!"changed".equals(action)) {
            return; // faqat "changed" bizni qiziqtiradi (new -> accepted/rejected)
        }
        // Webhook o'zi yangi statusni bermaydi - shuning uchun Poster API'dan joriy holatni so'raymiz
        orderReconciliationService.reconcileSingleOrder(posterIncomingOrderId);
    }

    private void handleTransactionChanged(String action, long posterTransactionId) {
        // Kelajakda kerak bo'lsa: order ACCEPTED bo'lgach yopilgan chekni shu yerda qayta ishlash mumkin.
        log.debug("transaction webhook: action={}, id={} (hozircha ishlanmaydi)", action, posterTransactionId);
    }

    private void handleApplicationEvent(String action, JsonNode data) {
        // OAuth ishlatilsa: action=added bo'lganda data.access_token va data.user_id keladi -
        // shu yerda akkauntga tegishli tokenni DB'ga saqlash logikasi qo'shiladi (ko'p-mijozli holat uchun).
        if ("added".equals(action)) {
            log.info("Ilova akkauntga ulandi. user_id={}, access_token mavjud={}",
                    data.path("user_id").asText(null), data.hasNonNull("access_token"));
        } else if ("removed".equals(action)) {
            log.info("Ilova akkauntdan uzildi");
        }
    }
}
