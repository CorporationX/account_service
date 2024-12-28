package faang.school.accountservice.repository;

import faang.school.accountservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId AND t.account.tariffId = :tariffId " +
            "AND t.type = 'EXPENSE' AND t.date BETWEEN :startDate AND :endDate")
    List<Transaction> findExpenseTransactionsForAccountAndTariff(Long accountId, Long tariffId, LocalDate startDate, LocalDate endDate);
}