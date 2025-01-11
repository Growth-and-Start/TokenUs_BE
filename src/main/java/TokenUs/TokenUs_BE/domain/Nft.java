package TokenUs.TokenUs_BE.domain;

import TokenUs.TokenUs_BE.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Nft extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String contractAddress;

    private Integer tokenId;

    private Float currentPrice;

    private Integer mintQuantity;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isListed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;
}
