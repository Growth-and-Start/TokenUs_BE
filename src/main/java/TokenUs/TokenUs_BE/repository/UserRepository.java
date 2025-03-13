package TokenUs.TokenUs_BE.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Email로 유저 검색
    Optional<User> findByEmail(String email);
}
