package faang.school.accountservice.repository;

import faang.school.accountservice.model.number.AccountUniqueNumberCounter;
import faang.school.accountservice.enums.AccountNumberType;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountUniqueNumberCounter, String> {


    @Query(nativeQuery = true, value = """
            UPDATE account_unique_number_counter
            SET counter = counter + 1
            WHERE type = :type
            RETURNING counter;
            """)
    long incrementAndGet(@Param("type") String type);

    @Query(nativeQuery = true, value = """
            UPDATE account_unique_number_counter
            SET counter = counter + :increment
            WHERE type = :type
            RETURNING counter;
            """)
    long incrementToValueAndGet(@Param("type") String type, @Param("increment") long increment);
}
