package uz.poster.integration.order.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uz.poster.integration.order.service.OrderService;
import uz.poster.integration.poster_client.dto.OrderCreateRequest;
import uz.poster.integration.poster_client.dto.OrderCreateResponse;
import uz.poster.integration.poster_client.remote.PosterClient;
import uz.poster.integration.security.CustomUserDetails;

@Component
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final PosterClient posterClient;


    @Override
    public Boolean createOrder(OrderCreateRequest request, CustomUserDetails userDetails) {
            OrderCreateResponse res = posterClient.createOnlineOrder(request);
            Long orderId = res.getOrderId();
            System.out.println("Order ID: " + orderId);
            System.out.println(res);
            return true;

    }
}
