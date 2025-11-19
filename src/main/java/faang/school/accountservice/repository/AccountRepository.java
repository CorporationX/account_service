package faang.school.accountservice.repository;

import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    default Account getByIdOrThrow(long accountId) {
        return findById(accountId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Account %d not found", accountId))
        );
    }
}
