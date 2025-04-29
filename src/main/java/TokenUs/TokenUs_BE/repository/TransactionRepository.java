package TokenUs.TokenUs_BE.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.Transaction;
import TokenUs.TokenUs_BE.domain.enums.TransactionType;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(
            "SELECT t FROM Transaction t "
                    + "WHERE t.nft.video.id = :videoId "
                    + "AND t.type = :type "
                    + "ORDER BY t.createdAt DESC")
    List<Transaction> findByVideoIdAndTypeOrderByCreatedAtDesc(
            @Param("videoId") Long videoId, @Param("type") TransactionType type);
}
