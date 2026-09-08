package uz.poster.integration.poster_client.entity.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.poster.integration.common.entity.AuditableEntity;
import uz.poster.integration.poster_client.entity.spot.Spots;

import java.math.BigDecimal;

@Entity
@Table(
        name = "product_spots",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_product_spots_product_spot",
                        columnNames = {"product_id", "spot_id"}
                )
        }
)
@Getter
@Setter
public class ProductSpot extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spots spot;

    @Column(nullable = false)
    private BigDecimal price;

    private BigDecimal profit;

    private BigDecimal profitNetto;

    private Boolean visible;
}