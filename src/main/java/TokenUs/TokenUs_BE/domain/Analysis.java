package TokenUs.TokenUs_BE.domain;

import jakarta.persistence.*;

import lombok.*;

import TokenUs.TokenUs_BE.domain.common.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Analysis extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float similarityScore;

    @Column(length = 500)
    private String summaryText;

    @Column(length = 300)
    private String summaryGif;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;
}
