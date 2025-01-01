package faang.school.accountservice.repository;

import faang.school.accountservice.model.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long> {

    Optional<PaymentAccount> findByAccountNumber(String accountNumber);

    boolean existsById(Long id);
}
