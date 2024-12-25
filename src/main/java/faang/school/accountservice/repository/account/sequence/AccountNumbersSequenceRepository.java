package faang.school.accountservice.repository.account.sequence;
import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.model.account.sequence.AccountSeq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import faang.school.accountservice.model.account.AccountType;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSeq, AccountType> {
    @Modifying
    @Query("UPDATE AccountSeq a SET a.counter = a.counter + :batchSize WHERE a.type = :type AND a.counter = :currentCounter")
    int incrementCounter(@Param("type") AccountType type, @Param("currentCounter") Long currentCounter,@Param("batchSize") long batchSize);

    @Transactional
    default AccountSeq createCounter(AccountType type) {
        return save(new AccountSeq(type, 1L, null));
    }

    @Query("SELECT a FROM AccountSeq a WHERE a.type = :type")
    Optional<AccountSeq> findByType(@Param("type") AccountType type);
}
