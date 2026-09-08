package uz.poster.integration.poster_client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Poster'ning haqiqiy webhook payload formati (dev.joinposter.com/docs -> Webhooks).
 * Misol:
 * {
 *   "account": "api-demo",
 *   "account_number": "813932",
 *   "object": "incoming_order",
 *   "object_id": 1,
 *   "action": "changed",
 *   "time": "1518794257",
 *   "verify": "a23sk3d9123ka31sd3k5asd9123sad93",
 *   "data": { "type": 1 }
 * }
 *
 * DIQQAT: "changed" action faqat "nimadir o'zgardi" deydi - incoming_order uchun buyurtma
 * "new"dan "accepted" yoki "rejected"ga o'tganini bildiradi, lekin YANGI STATUS QIYMATINI
 * o'zi bermaydi. Aniq statusni bilish uchun webhook kelgach incomingOrders.getIncomingOrders
 * (yoki mos single-get) chaqirib, joriy holatni API'dan olish kerak.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PosterWebhookPayload(
        String account,
        String account_number,
        String object,      // "transaction", "incoming_order", "product", "application" va h.k.
        Long object_id,
        String action,      // "added" | "changed" | "removed" | "transformed"
        String time,         // unix timestamp, STRING sifatida keladi
        String verify,
        JsonNode data         // ba'zi entity'larda bo'ladi (masalan incoming_order -> {"type": 1})
) {
}
