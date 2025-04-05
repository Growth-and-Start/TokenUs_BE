package TokenUs.TokenUs_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.domain.mapping.VideoLike;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    Long countByVideo(Video video);
}
