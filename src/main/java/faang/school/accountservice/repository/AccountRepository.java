package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("""
             SELECT a
             FROM Account a
             JOIN a.balance b
             JOIN FETCH a.cashbackTariff ct
             WHERE b.id = :balanceId
            """)
    Optional<Account> findAccountByBalanceIdWithCashbackTariff(Long balanceId);

}
