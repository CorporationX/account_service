package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumber.Key> {

    Optional<FreeAccountNumber> findFirstByKeyAccountTypeOrderByCreatedAtAsc(String accountType);

    Optional<FreeAccountNumber> findByKeyAccountNumber(String accountNumber);
}
