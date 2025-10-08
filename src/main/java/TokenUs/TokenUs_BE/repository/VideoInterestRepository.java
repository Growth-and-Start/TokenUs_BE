package TokenUs.TokenUs_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.domain.mapping.VideoInterest;

@Repository
public interface VideoInterestRepository extends JpaRepository<VideoInterest, Long> {
    Optional<VideoInterest> findByUserIdAndVideoId(Long userId, Long videoId);

    Long countByVideo(Video video);

    boolean existsByUserIdAndVideoId(Long userId, Long videoId);
}
