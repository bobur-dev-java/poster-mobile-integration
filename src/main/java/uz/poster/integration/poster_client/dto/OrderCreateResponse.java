package uz.poster.integration.poster_client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
// client_address, sex, birthday va h.k. - hammasini yozmadik, shular sabab crash bo'lmasin
public class OrderCreateResponse {

    @JsonProperty("incoming_order_id")
    private Long orderId;

    @JsonProperty("spot_id")
    private Long spotId;

    private int status; // 0 -> NEW,1->ACCEPTED,7->CANCELED

    @JsonProperty("transaction_id")
    private Long transactionId; // yaratilganda HAR DOIM null keladi - keyinroq kassir qabul qilgach to'ladi

    @JsonProperty("created_at")
    private String createdAt;
}