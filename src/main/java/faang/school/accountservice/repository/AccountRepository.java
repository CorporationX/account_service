package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("SELECT a FROM Account a WHERE a.cashbackTariff IS NOT NULL AND a.status = 'ACTIVE'")
    List<Account> findActiveAccountsWithCashbackTariff(Pageable pageable);
}