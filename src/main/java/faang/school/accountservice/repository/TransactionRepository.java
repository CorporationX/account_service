package faang.school.accountservice.repository;

import faang.school.accountservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.type = 'EXPENSE' " +
            "AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findExpenseTransactionsForAccount(UUID accountId, LocalDate startDate, LocalDate endDate);
}
