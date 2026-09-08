package uz.poster.integration.poster_client.entity.spot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import uz.poster.integration.common.entity.AuditableEntity;

@Entity
@Table(
        name = "spots",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_spots_poster_spot_id",
                        columnNames = "poster_spot_id"
                )
        }
)
@Getter
@Setter
public class Spots extends AuditableEntity {

    @Column(name = "poster_spot_id", nullable = false)
    private Long posterSpotId;

    private String name;

    private String address;

    private String latitude;

    private String longitude;

    private boolean deleted;
}
