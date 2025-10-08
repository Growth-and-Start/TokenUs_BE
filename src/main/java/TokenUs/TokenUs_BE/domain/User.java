package TokenUs.TokenUs_BE.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import jakarta.persistence.*;

import org.springframework.security.core.GrantedAuthority;

import lombok.*;

import TokenUs.TokenUs_BE.domain.common.BaseEntity;
import TokenUs.TokenUs_BE.domain.enums.Role;
import TokenUs.TokenUs_BE.domain.enums.Status;
import TokenUs.TokenUs_BE.domain.mapping.Subscribe;
import TokenUs.TokenUs_BE.domain.mapping.VideoComment;
import TokenUs.TokenUs_BE.domain.mapping.VideoLike;
import org.hibernate.annotations.Comment;

@Setter
@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("사용자의 이메일 주소, 사용자 식별 및 로그인 시 아이디의 역할")
    @Column(nullable = false, length = 40, unique = true)
    private String email;

    @Comment("비밀번호, 암호화 처리 됨")
    @Column(nullable = false)
    private String password;

    @Comment("사용자의 지갑 주소")
    @Column(columnDefinition = "TEXT", unique = true)
    private String walletAddress;

    @Comment("사용자의 닉네임. 중복 없음")
    @Column(nullable = false, length = 20, unique = true)
    private String nickname;

    @Comment("사용자 프로필 사진 파일의 S3 URL")
    @Column(columnDefinition = "TEXT")
    private String profile_image;

    @Comment("비활성 시간, 추후 삭제를 위해")
    private LocalDate inactiveDate;

    // 비밀번호 암호화
    public void encodePassword(String password) {
        this.password = password;
    }

    // Enum 타입
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'ACTIVE'")
    private Status status;

    @Enumerated(EnumType.STRING)
    private Role role;

    // 연관 관계
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VideoComment> videoCommentList = new ArrayList<>();

    @OneToMany(mappedBy = "subscriber", cascade = CascadeType.ALL)
    private List<Subscribe> subscribingList = new ArrayList<>(); // 내가 구독한 사람들

    @OneToMany(mappedBy = "target", cascade = CascadeType.ALL)
    private List<Subscribe> subscriberList = new ArrayList<>(); // 나를 구독한 사람들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VideoLike> videoLikeList = new ArrayList<>();

    // 회원가입 시 role 기본값 설정
    @PrePersist
    public void setDefaultValues() {
        if (this.role == null) {
            this.role = Role.USER;
        }
        if (this.status == null) {
            this.status = Status.ACTIVE;
        }
        if (this.profile_image == null) {
            this.profile_image = "https://your-default-image-url.com/default.jpg";
        }
    }

    // refresh token
    @Column(length = 500)
    private String refreshToken;

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public User(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.email = email;
        this.password = password;
        this.nickname = "default";
        this.role = Role.USER;
        this.profile_image = null;
        this.walletAddress = null;
    }
}
