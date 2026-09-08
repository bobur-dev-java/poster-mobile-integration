package uz.poster.integration.poster_client.entity.order;

public enum OrderStatus {

    /**
     * Mobil ilovada yaratildi, lekin Poster'ga hali yuborilmadi (masalan tarmoq xatosi tufayli)
     */
    PENDING_SYNC,

    /**
     * incomingOrders.createIncomingOrder muvaffaqiyatli chaqirildi, kassir hali qabul qilmagan
     */
    NEW,

    /**
     * Kassir/ofitsiant POS'da qabul qildi - shu paytda posterTransactionId to'ladi
     */
    ACCEPTED,

    /**
     * Bekor qilindi
     */
    CANCELLED,

    /**
     * Poster'ga yuborishda doimiy xatolik (retry limitidan oshdi)
     */
    FAILED
}
