package uz.poster.integration.poster_client.service.order.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uz.poster.integration.poster_client.remote.PosterClient;
import uz.poster.integration.order.service.OrderService;
import uz.poster.integration.poster_client.service.order.OrderReconciliationService;

import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderReconciliationServiceImpl implements OrderReconciliationService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final PosterClient posterClient;
    private final OrderService orderService;

    /**
     * Webhook kelgach chaqiriladi - faqat bitta order_id uchun joriy statusni tekshiradi.
     */
    @Override
    public void reconcileSingleOrder(long posterIncomingOrderId) {
//        // DIQQAT: agar Poster'da bitta order'ni ID bo'yicha to'g'ridan-to'g'ri olish endpoint'i
//        // (masalan incomingOrders.getIncomingOrder) mavjud bo'lsa, o'shani ishlating - bir dona
//        // so'rov o'rniga kunlik ro'yxatni filtlashdan ko'ra samaraliroq. Docs'da aniq bo'lmagani
//        // uchun hozircha getIncomingOrders + filter orqali qilyapmiz.
//        String today = LocalDate.now().format(DATE_FORMAT);
//        List<PosterIncomingOrder> orders = posterClient.getIncomingOrders(today, today);
//
//        Optional<PosterIncomingOrder> match = orders.stream()
//                .filter(o -> String.valueOf(posterIncomingOrderId).equals(o.incoming_order_id()))
//                .findFirst();
//
//        if (match.isEmpty()) {
//            log.warn("Webhook kelgan order_id={} bugungi ro'yxatda topilmadi - keyingi umumiy reconcile'da tekshiriladi", posterIncomingOrderId);
//            return;
//        }
//
//        PosterIncomingOrder order = match.get();
//        if (order.status() != null) {
//            orderService.updateStatusFromPoster(posterIncomingOrderId, order.status());
//        }
    }

    /**
     * Xavfsizlik uchun zaxira: webhook umuman yetib kelmasa, ochiq buyurtmalarni davriy tekshiradi.
     */
//    @Scheduled(fixedDelayString = "PT5M")
//    public void reconcileOpenOrders() {
//        Set<Long> openOrderIds = orderService.getOpenPosterOrderIds();
//        if (openOrderIds.isEmpty()) return;
//
//        String today = LocalDate.now().format(DATE_FORMAT);
//        List<PosterIncomingOrder> orders = posterClient.getIncomingOrders(today, today);
//
//        orders.stream()
//                .filter(o -> openOrderIds.contains(o.incoming_order_id()))
//                .forEach(o -> {
//                    if (o.status() != null) {
//                        orderService.updateStatusFromPoster(o.incoming_order_id(), o.status());
//                    }
//                });
//    }
}
