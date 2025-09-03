package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AccountNumberRepository extends JpaRepository<AccountNumber, UUID> {

        Optional<AccountNumber> findByAccountNumber(String accountNumber);
}
