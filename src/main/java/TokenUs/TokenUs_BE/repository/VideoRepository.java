package TokenUs.TokenUs_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    // Video_Url로 영상 검색
    Optional<Video> findByFileUrl(String fileUrl);

    // 모든 영상 찾기, 최신순 정렬
    List<Video> findAllByOrderByCreatedAtDesc();

    // 크리에이터로 영상 찾기, 최신순 정렬
    List<Video> findByCreatorOrderByCreatedAtDesc(User user);

    // (공개Only)Video_Url로 영상 검색
    List<Video> findAllByIsOpenTrueOrderByCreatedAtDesc();

    // (공개Only)크리에이터로 영상 찾기, 최신순 정렬
    List<Video> findByCreatorInAndIsOpenTrueOrderByCreatedAtDesc(List<User> users);

    // (공개Only)크리에이터로 영상 찾기, 최신순 정렬
    List<Video> findByIsOpenTrue();

    @Query(
            "SELECT v FROM Video v "
                    + "WHERE v.isOpen = true AND "
                    + "(LOWER(v.title) LIKE LOWER(CONCAT('%', :searchFor, '%')) "
                    + "OR LOWER(v.creator.nickname) LIKE LOWER(CONCAT('%', :searchFor, '%'))) "
                    + "ORDER BY v.createdAt DESC")
    List<Video> searchByTitleOrCreatorNickname(@Param("searchFor") String searchFor);
}
