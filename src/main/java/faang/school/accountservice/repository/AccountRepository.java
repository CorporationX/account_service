package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.exception.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByIdAndUserId(Long id, Long userId);

    default Account getByIdAndUserIdOrThrow(Long accountId, Long userId) {
        return findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Account %d not found for user %d", accountId, userId)));
    }
}
