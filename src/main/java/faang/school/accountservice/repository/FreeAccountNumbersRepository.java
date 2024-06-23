package faang.school.accountservice.repository;

import faang.school.accountservice.model.account_number.FreeAccountNumber;
import faang.school.accountservice.model.account_number.FreeAccountNumberId;
import faang.school.accountservice.model.enums.AccountType;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    Optional<FreeAccountNumber> findByIdType(AccountType type);

    void deleteById(@NonNull FreeAccountNumberId freeAccountNumberId);

    @Transactional
    default Optional<FreeAccountNumber> getAndDeleteFirst(AccountType accountType) {
        FreeAccountNumber freeAccountNumber = findByIdType(accountType)
                .orElse(null);

        if (freeAccountNumber == null) {
            return Optional.empty();
        }

        deleteById(freeAccountNumber.getId());
        return Optional.of(freeAccountNumber);
    }
}
