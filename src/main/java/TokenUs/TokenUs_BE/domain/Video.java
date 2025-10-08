package TokenUs.TokenUs_BE.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.*;

import TokenUs.TokenUs_BE.domain.common.BaseEntity;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Builder
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Video extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, length = 300)
    private String detail;

    @Column(nullable = false, length = 200)
    private String fileUrl;

    @Column(nullable = true, length = 200)
    private String thumbnailUrl;

    @Column(nullable = true)
    @ColumnDefault("true")
    private Boolean isOpen;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long views;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User creator;

    @OneToMany(mappedBy = "video", fetch = FetchType.LAZY)
    private List<Nft> nfts = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_video_id")
    private Video parentVideo;

    @OneToMany(mappedBy = "parentVideo", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Video> derivedVideos = new ArrayList<>();
}
