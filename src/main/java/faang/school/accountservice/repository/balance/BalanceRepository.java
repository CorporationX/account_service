package faang.school.accountservice.repository.balance;

import faang.school.accountservice.entity.balance.Balance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends CrudRepository<Balance, Long> {

}
