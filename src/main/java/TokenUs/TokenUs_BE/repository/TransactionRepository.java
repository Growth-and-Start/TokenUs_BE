package TokenUs.TokenUs_BE.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import TokenUs.TokenUs_BE.domain.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {}
