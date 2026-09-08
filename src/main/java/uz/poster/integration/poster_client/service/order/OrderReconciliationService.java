package uz.poster.integration.poster_client.service.order;

import org.springframework.stereotype.Service;

@Service
public interface OrderReconciliationService {
    void reconcileSingleOrder(long posterIncomingOrderId);
}
