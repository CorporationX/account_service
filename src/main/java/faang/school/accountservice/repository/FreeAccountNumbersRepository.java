package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.FreeAccountNumber;
import faang.school.accountservice.model.entity.FreeAccountNumberId;
import faang.school.accountservice.model.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {
    Optional<FreeAccountNumber> findByIdAccountType(AccountType accountType);

    @Transactional
    default Optional<FreeAccountNumber> findAndDeleteFirst(AccountType accountType) {
        FreeAccountNumber freeAccountNumber = findByIdAccountType(accountType).orElse(null);

        if (freeAccountNumber == null) {
            return Optional.empty();
        }

        deleteById(freeAccountNumber.getId());

        return Optional.of(freeAccountNumber);
    }
}
