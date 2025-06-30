package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSequence;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSequence, String> {
    @Query("SELECT a.counter FROM AccountSequence a WHERE a.accountType = :accountType")
    Long getCurrentCounterByType(@Param("accountType") AccountType accountType);

    @Modifying
    @Query("UPDATE AccountSequence a SET a.counter = a.counter + :batchSize WHERE a.accountType = :accountType")
    int incrementCounter(@Param("accountType") AccountType accountType,
                         @Param("batchSize") int batchSize);
}