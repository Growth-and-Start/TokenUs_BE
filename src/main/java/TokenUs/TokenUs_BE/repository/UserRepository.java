package TokenUs.TokenUs_BE.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Email로 유저 검색
    Optional<User> findByEmail(String email);

    Optional<User> findByWalletAddress(String walletAddress);

    // 닉네임에 검색어가 포함되는 유저 리스트 (대소문자 무시)
    List<User> findByNicknameContainingIgnoreCase(String nickname);
}
