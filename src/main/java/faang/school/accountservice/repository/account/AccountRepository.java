package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserId(Long id);
    List<Account> findByProjectId(Long id);

}
