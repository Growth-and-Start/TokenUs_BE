package TokenUs.TokenUs_BE.domain.mapping;

import jakarta.persistence.*;

import lombok.*;

import TokenUs.TokenUs_BE.domain.Nft;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_nft_like",
                    columnNames = {"user_id", "nft_id"})
        })
public class NftLike extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nft_id", nullable = false)
    private Nft nft;
}
