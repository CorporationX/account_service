package faang.school.accountservice.repository;

import faang.school.accountservice.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, UUID> {
    @Query("""
            SELECT CASE WHEN COUNT(sa) > 0 THEN true ELSE false END
            FROM SavingsAccount sa
            JOIN sa.account acc
            WHERE acc.userId = :userId
               OR acc.projectId = :projectId
            """)
    boolean existsByOwnerId(Long userId, Long projectId);

    @Query("""
            SELECT sa FROM SavingsAccount sa
            JOIN sa.account acc
            WHERE acc.userId = :id
            """)
    List<SavingsAccount> findByUserId(Long id);

    @Query("""
            SELECT sa FROM SavingsAccount sa
            JOIN sa.account acc
            WHERE acc.projectId = :id
            """)
    List<SavingsAccount> findByProjectId(Long id);

    @Query("""
            SELECT sa FROM SavingsAccount sa
            JOIN sa.account acc
            WHERE acc.status = 'ACTIVE'
                AND FUNCTION('date', sa.createdAt) <> CURRENT_DATE
                AND (sa.lastInterestAt IS NULL OR FUNCTION('date', sa.lastInterestAt) <> CURRENT_DATE)
            """)
    List<SavingsAccount> findAllForAccrueInterest();
}
