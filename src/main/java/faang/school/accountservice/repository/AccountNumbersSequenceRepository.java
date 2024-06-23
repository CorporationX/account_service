package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountNumbersSequence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, Long> {
    Optional<AccountNumbersSequence> findByAccountType(String accountType);
}
