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

    Optional<Video> findByFileUrl(String fileUrl);

    Optional<Video> findById(Long videoId);

    List<Video> findAllByOrderByCreatedAtDesc();

    List<Video> findByCreatorOrderByCreatedAtDesc(User user);

    List<Video> findAllByIsOpenTrueOrderByCreatedAtDesc();

    List<Video> findByCreatorInAndIsOpenTrueOrderByCreatedAtDesc(List<User> users);

    List<Video> findByIsOpenTrue();

    List<Video> findAllByIsOpenTrueOrderByViewsDesc();

    Video findTopByIsOpenTrueOrderByViewsDesc();

    @Query(
            "SELECT v FROM Video v "
                    + "WHERE v.isOpen = true AND "
                    + "(LOWER(v.title) LIKE LOWER(CONCAT('%', :searchFor, '%')) "
                    + "OR LOWER(v.creator.nickname) LIKE LOWER(CONCAT('%', :searchFor, '%'))) "
                    + "ORDER BY v.createdAt DESC")
    List<Video> searchByTitleOrCreatorNickname(@Param("searchFor") String searchFor);
}
