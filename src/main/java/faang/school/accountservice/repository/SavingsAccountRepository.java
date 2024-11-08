package faang.school.accountservice.repository;


import faang.school.accountservice.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    @Query("""
            SELECT s FROM SavingsAccount s
            JOIN Account a
            WHERE a.holderUserId = :userId
            """)
    Optional<SavingsAccount> findByUserId(long userId);
}
