package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.enums.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, String> {

    @Query(nativeQuery = true, value = """
                    UPDATE account_numbers_sequence SET count = count + :batchSize
                    where type = :type
                    RETURNING type, count
            """)
    AccountNumberSequence incrementAndGet(String type, int batchSize);

    boolean existsAccountNumberSequenceByType(CardType type);
}
