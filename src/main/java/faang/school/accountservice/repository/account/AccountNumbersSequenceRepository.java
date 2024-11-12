package faang.school.accountservice.repository.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import faang.school.accountservice.entity.account.AccountNumbersSequence;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, String> {

    @Modifying
    @Query(
        nativeQuery = true,
        value = """
            UPDATE account_numbers_sequence
            SET counter = counter + :batchSize
            WHERE type = :type
        """
    )
    int updateCounterForType(String type, int batchSize);

    @Query(
        nativeQuery = true,
        value = """
            SELECT *
            FROM account_numbers_sequence
            WHERE type = :type
        """
    )
    Optional<AccountNumbersSequence> findCounterByType(String type);
    
}
