package TokenUs.TokenUs_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.Nft;

@Repository
public interface NftRepository extends JpaRepository<Nft, Integer> {

    boolean existsByVideoIdAndOwnerId(Long videoId, Long ownerId);
}
