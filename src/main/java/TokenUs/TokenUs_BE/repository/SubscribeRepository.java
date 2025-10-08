package TokenUs.TokenUs_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.mapping.Subscribe;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    boolean existsBySubscriberIdAndTargetId(Long subscriberId, Long targetId);

    Optional<Subscribe> findBySubscriberIdAndTargetId(Long subscriberId, Long targetId);
}
