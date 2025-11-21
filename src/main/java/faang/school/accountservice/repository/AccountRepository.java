package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    default Account findAccountByIdOrThrow(UUID accountId) {
        return findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Account %s not found", accountId)));
    }
}
