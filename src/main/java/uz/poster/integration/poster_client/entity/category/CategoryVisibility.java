package uz.poster.integration.poster_client.entity.category;

import jakarta.persistence.*;
import lombok.*;
import uz.poster.integration.common.entity.AuditableEntity;
import uz.poster.integration.poster_client.entity.spot.Spots;

@Entity
@Table(
        name = "category_visibility",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_category_visibility_category_spot",
                        columnNames = {"category_id", "spot_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryVisibility extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spots spots;

    @Column(name = "visible", nullable = false)
    private Boolean visible;
}