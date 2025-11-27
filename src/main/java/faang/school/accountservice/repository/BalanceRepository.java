package faang.school.accountservice.repository;

import faang.school.accountservice.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {

    @Query("SELECT b FROM Balance b WHERE b.account.id = :accountId")
    Balance findByAccountId(long accountId);
}