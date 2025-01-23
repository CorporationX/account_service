package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

    @Nullable
    FreeAccountNumber findFirstByIdType(AccountType type);
}
