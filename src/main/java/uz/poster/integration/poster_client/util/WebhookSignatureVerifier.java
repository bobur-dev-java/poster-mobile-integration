package uz.poster.integration.poster_client.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Poster webhook'idagi "verify" maydonini tekshiradi.
 * Poster docs (PHP misoli)dagi formula:
 * md5( account ; object ; object_id ; action ; [data ;] time ; application_secret )
 * <p>
 * "data" faqat payloadda mavjud bo'lsagina hash'ga qo'shiladi.
 * <p>
 * DIQQAT: "data" obyektini qanday serialize qilib hash'ga qo'shishi Poster tomonida
 * aniq hujjatlashtirilmagan (docs faqat PHP array'ni implode qilyapti, bu json_encode bilan
 * bir xil emas). Shu sababli productionga chiqarishdan oldin BUNI HAQIQIY WEBHOOK bilan sinab
 * ko'ring - agar data bor payload'larda (masalan incoming_order) verify mos kelmasa,
 * pastdagi buildDataPart() qismini moslashtirish kerak bo'lishi mumkin. account/object/object_id/
 * action/time bilan hisoblangan (data'siz) hodisalar uchun bu 100% ishlaydi.
 */
public final class WebhookSignatureVerifier {

    private WebhookSignatureVerifier() {
    }

    public static boolean isValid(JsonNode payload, String applicationSecret) {
        if (applicationSecret == null || applicationSecret.isBlank()) {
            // Secret sozlanmagan - tekshiruvsiz o'tkazamiz (faqat dev/test uchun!)
            return true;
        }

        String providedVerify = payload.path("verify").asText(null);
        if (providedVerify == null) {
            return false;
        }

        StringBuilder raw = new StringBuilder();
        raw.append(payload.path("account").asText(""));
        raw.append(';').append(payload.path("object").asText(""));
        raw.append(';').append(payload.path("object_id").asText(""));
        raw.append(';').append(payload.path("action").asText(""));

        JsonNode data = payload.get("data");
        if (data != null && !data.isNull()) {
            raw.append(';').append(data.toString()); // compact JSON, masalan {"type":1}
        }

        raw.append(';').append(payload.path("time").asText(""));
        raw.append(';').append(applicationSecret);

        String computed = md5Hex(raw.toString());
        return computed.equalsIgnoreCase(providedVerify);
    }

    private static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("MD5 mavjud emas", e);
        }
    }
}
