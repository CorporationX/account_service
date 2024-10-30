package faang.school.accountservice.repository;

import faang.school.accountservice.entity.PaymentAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long> {

    @Transactional(readOnly = true)
    Optional<PaymentAccount> findById(Long accountId);

    @Transactional
    @Modifying
    void deleteById(Long id);

    Optional<PaymentAccount> findByUserId(Long userId);
}
