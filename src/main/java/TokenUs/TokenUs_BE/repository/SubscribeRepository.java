package TokenUs.TokenUs_BE.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.mapping.Subscribe;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    @Query("SELECT s.subscribedTo FROM Subscribe s WHERE s.subscribedFrom.id = :subscriberId")
    List<User> findSubscribedUsersBySubscriberId(@Param("subscriberId") Long subscriberId);
}
