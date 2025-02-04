package TokenUs.TokenUs_BE.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;

import lombok.*;

import TokenUs.TokenUs_BE.domain.common.BaseEntity;
import TokenUs.TokenUs_BE.domain.enums.Status;
import TokenUs.TokenUs_BE.domain.mapping.Subscribe;
import TokenUs.TokenUs_BE.domain.mapping.VideoComment;
import TokenUs.TokenUs_BE.domain.mapping.VideoLike;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String email;

    @Column(nullable = false, length = 200)
    private String walletAddress;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 200)
    private String profile_image;

    private LocalDate inactiveDate;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private Status status;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VideoComment> videoCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "subscribedFrom", cascade = CascadeType.ALL)
    private List<Subscribe> subscribedFromList = new ArrayList<>();

    @OneToMany(mappedBy = "subscribedTo", cascade = CascadeType.ALL)
    private List<Subscribe> subscribedToList = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VideoLike> videoLikeList = new ArrayList<>();
}
