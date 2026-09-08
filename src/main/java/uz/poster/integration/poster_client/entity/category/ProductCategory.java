package uz.poster.integration.poster_client.entity.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import uz.poster.integration.common.entity.AuditableEntity;

@Entity
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_category_poster",
                        columnNames = {"poster_category_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory extends AuditableEntity {

    @Column(name = "poster_category_id", nullable = false)
    private Long posterCategoryId;

    @Column(nullable = false)
    private String name;

    private String photo;

    private String photoOrigin;

    private Long parentCategory;

    private String color;

    private Boolean hidden;

    private Integer sortOrder;

    private Boolean fiscal;

    private Boolean noDiscount;

    private Long taxId;

    @Column(name = "left_position")
    private Integer left;

    @Column(name = "right_position")
    private Integer right;

    private Integer level;

    private String categoryTag;

    private Long masterId;

    private String id1c;
}
