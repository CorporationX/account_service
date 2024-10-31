package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    Optional<SavingsAccount> findByOwnerId(Long ownerId);

    @Query("SELECT sa FROM SavingsAccount sa WHERE sa.lastInterestDate IS NULL OR sa.lastInterestDate < CURRENT_DATE")
    List<SavingsAccount> findAllReadyToInterestCalculation();
}
