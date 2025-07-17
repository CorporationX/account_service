package faang.school.accountservice.repository;

import faang.school.accountservice.entity.SavingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingAccount, UUID> {
}
