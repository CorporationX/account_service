package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.AccountNumberSequence;
import faang.school.accountservice.model.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {

    @Query(value = """
            UPDATE AccountNumberSequence ans
            SET ans.COUNTER = ans.COUNTER + 1
            WHERE ans.ACCOUNT_TYPE = :accountType
            RETURNING counter
            """)
    @Modifying
    Long getNextSequence(@Param("accountType") AccountType accountType);
}
