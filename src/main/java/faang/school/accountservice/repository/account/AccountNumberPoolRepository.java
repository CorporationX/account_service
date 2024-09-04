package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.AccountNumberPool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AccountNumberPoolRepository extends JpaRepository<AccountNumberPool, UUID> {
    AccountNumberPool findTopByOrderByIdAsc();
    void deleteByAccountNumber(String accountNumber);
}
