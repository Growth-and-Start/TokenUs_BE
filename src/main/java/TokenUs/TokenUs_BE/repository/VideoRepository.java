package TokenUs.TokenUs_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    // Video_Url로 영상 검색
    Optional<Video> findByFileUrl(String fileUrl);
}
