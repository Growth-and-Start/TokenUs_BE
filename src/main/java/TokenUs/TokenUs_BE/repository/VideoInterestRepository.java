package TokenUs.TokenUs_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.Video;
import TokenUs.TokenUs_BE.domain.mapping.VideoInterest;

@Repository
public interface VideoInterestRepository extends JpaRepository<VideoInterest, Long> {
    Optional<VideoInterest> findByUserIdAndVideoId(Long userId, Long videoId);

    // 비디오에 대한 관심 수 계산
    Long countByVideo(Video video);

    // 사용자가 특정 비디오에 관심을 표시했는지 확인
    boolean existsByUserIdAndVideoId(Long userId, Long videoId);
}
