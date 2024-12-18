package faang.school.accountservice.repository;

import faang.school.accountservice.entiry.AccountNumberSequence;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountNumbersSequenceRepository {

    @Transactional
    @Modifying
    @Query(value = "UPDATE account_numbers_sequence " +
            "SET current_value = current_value + 1 " +
            "WHERE account_type = :accountType " +
            "RETURNING *", nativeQuery = true)
    AccountNumberSequence incrementAndGetByAccountType(@Param("accountType") String accountType);
}