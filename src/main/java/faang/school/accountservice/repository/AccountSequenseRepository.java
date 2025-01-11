package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSequenseRepository extends JpaRepository<AccountSequence, Long> {
    @Modifying
    @Query(nativeQuery = true,
            value = """
                    UPDATE account_numbers_sequence SET counter = counter + :butchSize
                    WHERE type = :type
                    RETURNING type, counter, old.counter as initialCounter
                    """)
    AccountSequence incrementCounter(String type, int butchSize);
}
