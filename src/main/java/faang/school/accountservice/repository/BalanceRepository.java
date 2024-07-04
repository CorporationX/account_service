package faang.school.accountservice.repository;

import faang.school.accountservice.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {

    @Query("SELECT b FROM Balance b JOIN b.account a WHERE a.number = :accountNumber")
    Optional<Balance> findBalanceByAccountNumber(String accountNumber);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Balance b JOIN b.account a JOIN a.owner o WHERE b.id = :balanceId AND o.accountOwnerId = :userId AND o.accountId = a.id")
    Boolean isBalanceOwner(Long balanceId, Long userId);
}