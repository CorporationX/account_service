package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {

    @Query(nativeQuery = true, value = """
            select exists ( select 1 from balance d where account_id = :id)
            """)
    boolean existsByAccountId(Long id);
}
