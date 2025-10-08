package TokenUs.TokenUs_BE.domain.mapping;

import jakarta.persistence.*;

import lombok.*;

import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.common.BaseEntity;

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
    @JoinColumn(name = "subscriber_id")
    private User subscriber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id")
    private User target;
}
