package faang.school.accountservice.repository.jpa;

import faang.school.accountservice.entity.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumbersJpaRepository extends JpaRepository<FreeAccountNumber, Long> {

    @Query(nativeQuery = true, value = """
                    SELECT * FROM free_account_numbers
                    WHERE type = :type
                    LIMIT 1
            """)
    Optional<FreeAccountNumber> getReferenceByType(String type);

    void deleteById(Long id);
}
