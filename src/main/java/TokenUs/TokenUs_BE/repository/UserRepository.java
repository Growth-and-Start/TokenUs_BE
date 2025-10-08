package TokenUs.TokenUs_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByWalletAddress(String walletAddress);

    List<User> findByNicknameContainingIgnoreCase(String nickname);
}
