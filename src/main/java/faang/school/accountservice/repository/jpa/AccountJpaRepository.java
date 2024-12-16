package faang.school.accountservice.repository.jpa;

import faang.school.accountservice.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, Long> {

    Account findAccountById(long id);

    List<Account> findAllAccountsByOwnerId(long id);
}
