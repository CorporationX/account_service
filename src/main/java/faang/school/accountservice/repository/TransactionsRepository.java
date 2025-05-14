package faang.school.accountservice.repository;

import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.Transaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionsRepository extends JpaRepository<Transaction, Long> {
    @EntityGraph(attributePaths = "transactions")
    @Query("SELECT b FROM Balance b WHERE b.id = :balanceId")
    Optional<Balance> findWithTransactionsById(Long balanceId);

    @Query("SELECT new faang.school.accountservice.dto.TransactionDto(t.type, t.amount) FROM Transaction t WHERE t.balance.id = :balanceId")
    List<TransactionDto> findDtoByBalanceId(Long balanceId);
}
