package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByOwnerIdAndOwnerType(long ownerId, String ownerType);

    boolean existsByNumber(String number);

    Optional<Account> findByNumber(String number);
}
