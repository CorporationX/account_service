package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccNumSequenceRepository extends JpaRepository<AccountNumbersSequence, String> {

    @Query(nativeQuery = true, value = """
            insert into account_numbers_sequence (account_type,counter) values (:type,1)
            on conflict do nothing;
            """)
    @Modifying
    void createCounterIfNotExist(@Param(value = "type") String type);

    @Query(nativeQuery = true, value = """
            update account_numbers_sequence set counter = counter + :batchSize where account_type = :type
            RETURNING account_type,counter;
            """)
    AccountNumbersSequence increaseCounter(@Param(value = "type") String type, @Param(value = "batchSize") long batchSize);

    @Query(nativeQuery = true, value = """
            select acc.counter from account_numbers_sequence as acc where acc.account_type = :type
            """)
    Optional<Long> findCounterByType(@Param(value = "type") String type);

}
