package faang.school.accountservice.repository;

import faang.school.accountservice.entity.PaymentAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentAccountRepository extends CrudRepository<PaymentAccount, Long> {
}
