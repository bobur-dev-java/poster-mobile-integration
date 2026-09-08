package uz.poster.integration.poster_client.entity.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import uz.poster.integration.common.entity.AuditableEntity;

import java.math.BigDecimal;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_poster_id",
                        columnNames = "poster_product_id"
                )
        }
)
@Getter
@Setter
public class Product extends AuditableEntity {

    @Column(name = "poster_product_id", nullable = false)
    private Long posterProductId;

    private String barcode;

    private String name;

    private String unit;

    private BigDecimal cost;

    private BigDecimal costNetto;

    private Boolean fiscal;

    private Boolean hidden;

    private Long menuCategoryId;

    private Integer workshop;

    private Boolean noDiscount;

    private String photo;

    private String photoOrigin;

    private String productCode;

    private Integer sortOrder;

    private Long taxId;

    private Long productTaxId;

    private Integer type;

    private Boolean weightFlag;

    private String color;

    private Long ingredientId;

    private Boolean differentSpotsPrices;

    private Long masterId;
}
