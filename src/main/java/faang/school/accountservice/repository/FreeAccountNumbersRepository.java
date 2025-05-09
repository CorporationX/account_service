package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumber.Key> {

    Optional<FreeAccountNumber> findFirstByKeyAccountTypeOrderByCreatedAtAsc(String accountType);

    Optional<FreeAccountNumber> findByKeyAccountNumber(String accountNumber);
}
