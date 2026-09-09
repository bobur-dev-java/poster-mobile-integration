package uz.poster.integration.poster_client.remote;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import uz.poster.integration.config.properties.PosterProperties;
import uz.poster.integration.poster_client.dto.OrderCreateRequest;
import uz.poster.integration.poster_client.dto.OrderCreateResponse;
import uz.poster.integration.poster_client.dto.PosterApiResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class PosterClient {

    private final RestClient restClient; // baseUrl = https://joinposter.com/api, config'da bean qiling
    private final PosterProperties posterProperties;

    public OrderCreateResponse createOnlineOrder(OrderCreateRequest request) {
        try {
            PosterApiResponse<OrderCreateResponse> response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/incomingOrders.createIncomingOrder")
                            .queryParam("token", posterProperties.token())
                            .build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .requiredBody(new ParameterizedTypeReference<PosterApiResponse<OrderCreateResponse>>() {
                    });

            if (response == null) {
                log.error("Poster API'dan bo'sh javob keldi");
            }
            if (response.hasError()) {
                log.error("Error: {}", response.error());
            }
            return response.response();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}