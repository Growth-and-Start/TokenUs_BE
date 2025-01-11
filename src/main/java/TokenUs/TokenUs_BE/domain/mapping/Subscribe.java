package TokenUs.TokenUs_BE.domain.mapping;

import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Subscribe extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="subscribed_from_id")
    private User subscribedFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="subscribed_to_id")
    private User subscribedTo;
}
