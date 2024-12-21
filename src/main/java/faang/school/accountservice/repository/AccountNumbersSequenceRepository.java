package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {

    Optional<AccountNumberSequence> findByAccountType(AccountType accountType);
}