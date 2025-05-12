package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.enums.AccountType;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountSeq, AccountType> {

    @Query(
            nativeQuery = true,
            name = "AccountSeq.incrementCounter"
    )
    AccountSeq incrementCounter(
            @Param("type") String type,
            @Param("batchSize") int batchSize
    );

    AccountSeq findByAccountType(AccountType type);
}
