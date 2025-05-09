package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, String> {
}
