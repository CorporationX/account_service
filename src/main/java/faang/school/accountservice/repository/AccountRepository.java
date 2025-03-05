package faang.school.accountservice.repository;

import faang.school.accountservice.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}