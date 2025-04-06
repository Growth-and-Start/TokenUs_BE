package TokenUs.TokenUs_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.domain.mapping.VideoLike;

@Repository
public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    Long countByVideo(Video video);

    // 좋아요 여부 확인
    boolean existsByUserIdAndVideoId(Long userId, Long videoId);

    Optional<VideoLike> findByUserIdAndVideoId(Long userId, Long videoId);
}
