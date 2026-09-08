package uz.poster.integration.poster_client.entity.order;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.poster.integration.common.entity.AuditableEntity;
import uz.poster.integration.poster_client.entity.product.Product;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Nullable qoldirilgan - agar mahsulot keyinchalik Poster'da o'chirilsa,
     * eski buyurtma tarixi FK xatosiga uchramasligi kerak. Shu uchun ham snapshot maydonlari bor.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    /** Buyurtma paytidagi mahsulot nomi - mahsulot keyin o'zgarsa/o'chirilsa ham tarix to'g'ri qolishi uchun */
    @Column(nullable = false)
    private String productNameSnapshot;

    /** Buyurtma paytidagi narx (bir dona uchun) */
    @Column(nullable = false)
    private BigDecimal priceSnapshot;

    private Integer type;

    private Long workshopId;

    @Column(nullable = false)
    private BigDecimal quantity;

    private BigDecimal productSum;

    private BigDecimal payedSum;

    private BigDecimal certSum;

    private BigDecimal bonusSum;

    private BigDecimal bonusAccrual;

    private BigDecimal roundSum;

    private BigDecimal discount;

    private Long fiscalCompanyId;

    private Boolean printFiscal;

    private Long taxId;

    private BigDecimal taxValue;

    private Integer taxType;

    private BigDecimal taxFiscal;

    private BigDecimal taxSum;

    private BigDecimal productCost;

    private BigDecimal productProfit;

    private BigDecimal productCostNetto;

    private BigDecimal productProfitNetto;
}