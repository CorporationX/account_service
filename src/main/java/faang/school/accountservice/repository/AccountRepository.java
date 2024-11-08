package faang.school.accountservice.repository;

import faang.school.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    boolean existsAccountByAccountNumber(String accountNumber);
}
