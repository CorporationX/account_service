package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM balance WHERE id = ?1 FOR UPDATE
            """)
    Optional<Balance> findByIdForUpdate(Long id);
}
