package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.balance.AuthorizationBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuthorizationBalanceRepository extends JpaRepository<AuthorizationBalance, UUID> {

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM authorization_balance
            WHERE type = 'AUTHORIZED' AND expires_at < CURRENT_TIMESTAMP
            """)
    List<AuthorizationBalance> getExpiredAuthorizationBalance();
}
