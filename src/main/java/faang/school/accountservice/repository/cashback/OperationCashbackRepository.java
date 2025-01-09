package faang.school.accountservice.repository.cashback;

import faang.school.accountservice.model.cashback.CashbackId;
import faang.school.accountservice.model.cashback.OperationCashback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OperationCashbackRepository extends JpaRepository<OperationCashback, CashbackId> {
}