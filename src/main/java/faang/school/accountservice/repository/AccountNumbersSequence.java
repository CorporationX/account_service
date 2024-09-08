package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountNumbersSequence extends JpaRepository<Account, AccountType> {
}
