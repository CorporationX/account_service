package faang.school.accountservice.repository;

import faang.school.accountservice.model.Balance;
import org.springframework.data.repository.CrudRepository;

public interface BalanceRepository extends CrudRepository<Balance, Long> {
}