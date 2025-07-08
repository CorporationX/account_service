package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.AccountNumbersSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, AccountType> {

    @Modifying
    @Transactional
    @Query("UPDATE AccountNumbersSequence a SET a.currentValue = a.currentValue + 1 " +
            "WHERE a.accountType = :accountType AND a.currentValue = :expectedValue")
    int incrementSequence(@Param("accountType") AccountType accountType,
                          @Param("expectedValue") Long expectedValue);

    @Query("SELECT a FROM AccountNumbersSequence a WHERE a.accountType = :accountType")
    Optional<AccountNumbersSequence> findByAccountType(@Param("accountType") AccountType accountType);

    @Query("SELECT a.currentValue FROM AccountNumbersSequence a WHERE a.accountType = :accountType")
    Optional<Long> getCurrentValue(@Param("accountType") AccountType accountType);
}