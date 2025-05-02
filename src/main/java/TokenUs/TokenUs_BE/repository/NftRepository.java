package TokenUs.TokenUs_BE.repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.Nft;

@Repository
public interface NftRepository extends JpaRepository<Nft, Long> {

    boolean existsByVideo_FileUrlAndOwner_Id(String videoUrl, Long ownerId);

    Optional<Nft> findByTokenId(BigInteger tokenId);

    List<Nft> findByOwnerId(Long ownerId);
}
