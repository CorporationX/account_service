package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByOwnerIdAndOwnerType(long ownerId, OwnerType ownerType);

    Optional<Account> findByNumber(String number);
}
