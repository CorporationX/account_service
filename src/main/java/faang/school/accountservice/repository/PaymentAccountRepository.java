package faang.school.accountservice.repository;

import faang.school.accountservice.entity.PaymentAccount;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentAccountRepository extends JpaRepository<PaymentAccount, Long> {
}
