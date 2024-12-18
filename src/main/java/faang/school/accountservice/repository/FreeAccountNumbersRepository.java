package faang.school.accountservice.repository;

import faang.school.accountservice.entiry.FreeAccountNumber;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, Long> {

    FreeAccountNumber getFirstByAccountType(AccountType accountType);
    void deleteById(Long accountId);
}