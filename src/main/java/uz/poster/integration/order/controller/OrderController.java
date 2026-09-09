package uz.poster.integration.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uz.poster.integration.common.dto.BaseResponse;
import uz.poster.integration.order.service.OrderService;
import uz.poster.integration.poster_client.dto.OrderCreateRequest;
import uz.poster.integration.security.CustomUserDetails;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<BaseResponse<Boolean>> createOrder(
            @RequestBody OrderCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Boolean result = orderService.createOrder(request, userDetails);

        return ResponseEntity.ok(
                BaseResponse.success(result)
        );
    }

}
