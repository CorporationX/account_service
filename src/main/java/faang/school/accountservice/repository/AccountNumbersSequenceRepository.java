package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, String> {

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence 
            SET previous_counter = current_counter,  
            current_counter = current_counter + :batchSize                    
            WHERE account_type = :accountType
            RETURNING account_type, current_counter, previous_counter, version
                
            """)
    AccountNumberSequence incrementCounter(String accountType, int batchSize);
}
