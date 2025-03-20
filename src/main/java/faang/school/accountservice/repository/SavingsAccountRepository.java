package faang.school.accountservice.repository;

import faang.school.accountservice.entity.SavingsAccount;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, UUID> {
    Optional<SavingsAccount> findById(UUID id);

    Optional<SavingsAccount> findByAccountId(Long accountId);

    List<SavingsAccount> findByLastInterestDateBefore(LocalDate toDay);
}
