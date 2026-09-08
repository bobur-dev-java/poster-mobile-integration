package uz.poster.integration.poster_client.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.poster.integration.common.entity.AuditableEntity;
import uz.poster.integration.poster_client.entity.spot.Spots;
import uz.poster.integration.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "orders",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_orders_idempotency_key",
                        columnNames = "idempotency_key"
                ),
                @UniqueConstraint(
                        name = "uk_orders_poster_incoming_order_id",
                        columnNames = "poster_incoming_order_id"
                ),
                @UniqueConstraint(
                        name = "uk_orders_poster_transaction_id",
                        columnNames = "poster_transaction_id"
                )
        }
)
@Getter
@Setter
public class Order extends AuditableEntity {

    /**
     * Mobil ilova generatsiya qiladigan noyob kalit - tarmoq uzilib qayta yuborilganda
     * bitta buyurtma ikki marta yaratilib ketmasligi uchun.
     */
    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    /** Buyurtmani berayotgan mobil ilova foydalanuvchisi (Poster clientId EMAS!) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "app_user_id", nullable = false)
    private User appUser;

    /**
     * incomingOrders.createIncomingOrder chaqirilganda darhol qaytadi.
     * Order yaratilgan paytda majburiy - shuning uchun nullable=false.
     */
    @Column(name = "poster_incoming_order_id", nullable = false)
    private Long posterIncomingOrderId;

    /**
     * Kassir POS'da qabul qilib, real chekka aylantirgandan KEYIN to'ladi.
     * Order yaratilgan paytda hali mavjud emas - shuning uchun NULLABLE.
     */
    @Column(name = "poster_transaction_id")
    private Long posterTransactionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING_SYNC;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spots spot;

    private Long tableId;

    /** Agar Poster CRM'idagi mijoz kartochkasiga ham bog'lamoqchi bo'lsangiz (ixtiyoriy, appUser'dan alohida) */
    private Long posterClientId;

    private String comment;

    private BigDecimal sum;

    private BigDecimal payedSum;

    private BigDecimal payedCash;

    private BigDecimal payedCard;

    private BigDecimal payedCert;

    private BigDecimal payedBonus;

    private BigDecimal payedThirdParty;

    private Integer payedCardType;

    private BigDecimal roundSum;

    private BigDecimal tipsCash;

    private BigDecimal tipsCard;

    private Integer payType;

    private Integer reason;

    private BigDecimal tipSum;

    private BigDecimal bonus;

    private BigDecimal discount;

    private Boolean printFiscal;

    private BigDecimal totalProfit;

    private BigDecimal totalProfitNetto;

    private LocalDateTime dateClose;

    private Boolean autoAccept;

    private Long applicationId;

    /** Poster'ga yuborishda necha marta urinilgani - retry mexanizmi uchun */
    @Column(nullable = false)
    private Integer syncAttempts = 0;

    private String lastSyncError;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }
}