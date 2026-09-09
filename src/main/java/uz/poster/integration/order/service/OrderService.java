package uz.poster.integration.order.service;

import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import uz.poster.integration.poster_client.dto.OrderCreateRequest;
import uz.poster.integration.security.CustomUserDetails;

@Service
public interface OrderService {
    Boolean createOrder(@Valid OrderCreateRequest request, CustomUserDetails userDetails);
}
