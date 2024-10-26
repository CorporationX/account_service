package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, Long> {

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = """
            DELETE FROM free_account_numbers
            WHERE id = (SELECT id FROM free_account_numbers
            WHERE account_type = ?1 LIMIT 1) RETURNING *
            """)
    Optional<FreeAccountNumber> findAndDeleteFirstFreeAccountNumberByType(String type);
}
