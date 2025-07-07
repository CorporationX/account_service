package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {
    @Query("SELECT fan FROM FreeAccountNumber fan WHERE fan.id.type = :accountType ORDER BY fan.id.accountNumber ASC")
    List<FreeAccountNumber> findAccountNumbersByType(@Param("accountType") AccountType type);

    default FreeAccountNumber findNextAccountNumberByType(AccountType type) {
        return findAccountNumbersByType(type).get(0);
    }
}