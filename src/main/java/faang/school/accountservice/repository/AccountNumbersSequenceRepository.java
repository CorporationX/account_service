package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountSeq;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSeq, AccountType> {

    @Modifying
    @Transactional
    @Query("UPDATE AccountSeq a SET a.counter = a.counter + :batchSize WHERE a.accountType = :accountType")
    void incrementCounter(AccountType accountType, int batchSize);

    AccountSeq findByAccountType(AccountType accountType);
}
