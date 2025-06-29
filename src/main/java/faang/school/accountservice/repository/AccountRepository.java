package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {

}
