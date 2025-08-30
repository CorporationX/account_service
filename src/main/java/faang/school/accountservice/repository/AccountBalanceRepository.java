package faang.school.accountservice.repository;

import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.model.AccountBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountBalanceRepository extends JpaRepository<AccountBalance, Long> {

    default AccountBalance getByIdOrThrow(Long accountId) {
        return findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    Optional<AccountBalance> findByIdAndUserId(Long accountId, Long userId);
}