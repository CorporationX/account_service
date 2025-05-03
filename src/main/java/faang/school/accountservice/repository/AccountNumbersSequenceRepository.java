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
            name = "AccountSeq.incrementCounter" // Ссылка на NamedNativeQuery
    )
    AccountSeq incrementCounter(
            @Param("type") String type,  // Принимает String, а не AccountType (из-за SQL)
            @Param("batchSize") int batchSize
    );

    AccountSeq findByAccountType(AccountType type);

}
