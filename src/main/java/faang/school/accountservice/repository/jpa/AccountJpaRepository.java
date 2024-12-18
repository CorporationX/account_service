package faang.school.accountservice.repository.jpa;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByIdAndStatus(long id, AccountStatus accountStatus);

    Optional<Account> findByAccountNumber(String accountNumber);
}
