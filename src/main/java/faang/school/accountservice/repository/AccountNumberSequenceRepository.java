package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountNumberType;
import faang.school.accountservice.model.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, Integer> {

    AccountNumberSequence findByType(AccountNumberType type);
}
