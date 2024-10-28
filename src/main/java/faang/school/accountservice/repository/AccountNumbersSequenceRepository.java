package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountUniqueNumberCounter;
import faang.school.accountservice.enums.AccountNumberType;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountUniqueNumberCounter, String> {

    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE account_unique_number_counter
            SET counter = counter + 1
            WHERE type = :accountNumberType
            RETURNING counter;
            """)
    long incrementAndGet(@Param("accountNumberType") AccountNumberType accountNumberType);


    @Modifying
    @Query(nativeQuery = true, value = """
            UPDATE account_unique_number_counter
            SET counter = counter + :increment
            WHERE type = :accountNumberType
            RETURNING counter;
            """)
    long incrementToValueAndGet(@Param("increment") long increment, @Param("accountNumberType") AccountNumberType accountNumberType);
}
