package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    Optional<FreeAccountNumber> findFirstByIdType(AccountType type);
}
