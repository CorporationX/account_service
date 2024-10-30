package faang.school.accountservice.repository;

import faang.school.accountservice.model.savings.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, UUID> {
}
