package TokenUs.TokenUs_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.mapping.Subscribe;

@Repository
public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    // 구독 여부 확인 (현재 로그인 유저 → 검색된 유저)
    boolean existsBySubscriberIdAndTargetId(Long subscriberId, Long targetId);

    Optional<Subscribe> findBySubscriberIdAndTargetId(Long subscriberId, Long targetId);
}
