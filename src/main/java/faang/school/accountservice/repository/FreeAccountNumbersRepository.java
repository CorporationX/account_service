package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = """
            DELETE FROM free_account_numbers
            WHERE account_type =:accountType
            ORDER BY account_number::BIGINT
            LIMIT 1
            RETURNING account_number
            """)
    Optional<FreeAccountNumber> getAndDeleteFirstFreeAccountNumber(@Param("accountType") String accountType);
}
