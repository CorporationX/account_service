package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    Optional<SavingsAccount> findByOwnerId(Long ownerId);

    List<SavingsAccount> findByLastInterestDateIsNullOrLastInterestDateLessThan(LocalDateTime currentDate);
}
