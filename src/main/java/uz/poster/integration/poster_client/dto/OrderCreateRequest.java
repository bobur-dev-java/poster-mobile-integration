package uz.poster.integration.poster_client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    @JsonProperty("spot_id")
    private Long spotId;
    private String phone; // Poster'da ham "phone" - o'zgarishsiz mos keladi
    private List<OrderProduct> products;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderProduct { // MUHIM: private emas, aks holda tashqaridan OrderCreateRequest.OrderProduct.builder() ishlamaydi
        @JsonProperty("product_id")
        private Long id;
        private int count; // og'irlik bo'yicha sotiladigan mahsulotlar uchun kasr son kerak bo'lsa, double qiling
    }
}