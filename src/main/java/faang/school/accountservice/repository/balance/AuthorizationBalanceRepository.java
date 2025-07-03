package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.balance.AuthorizationBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthorizationBalanceRepository extends JpaRepository<AuthorizationBalance, UUID> {
}
