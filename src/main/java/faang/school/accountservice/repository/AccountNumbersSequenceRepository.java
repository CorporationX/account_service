package faang.school.accountservice.repository;

import faang.school.accountservice.entity.account.AccountSeq;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumbersSequenceRepository
        extends JpaRepository<AccountSeq, AccountType> {

    /*
        we force flush changes to DB immediately -
        we don't need to wait for the transaction commit to perform optimistic locking
    */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE AccountSeq s
            SET s.counter = s.counter + :delta
            WHERE s.type = :type AND s.counter = :expected
            """)
    int tryIncrementCounter(@Param("type") AccountType type,
                            @Param("expected") long expected,
                            @Param("delta") int delta);
}