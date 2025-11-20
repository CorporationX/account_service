package faang.school.accountservice.repository;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.Currency;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    List<Account> findByUserId(Long userId, Pageable pageable);

    List<Account> findByProjectId(Long projectId, Pageable pageable);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCurrency(Currency currency);
}
