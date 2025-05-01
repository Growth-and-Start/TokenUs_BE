package TokenUs.TokenUs_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.Nft;
import TokenUs.TokenUs_BE.domain.User;
import TokenUs.TokenUs_BE.domain.mapping.NftLike;

@Repository
public interface NftLikeRepository extends JpaRepository<NftLike, Long> {
    boolean existsByUserAndNft(User user, Nft nft);

    void deleteByUserAndNft(User user, Nft nft);
}
