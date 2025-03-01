package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByOwnerIdAndOwnerType(long ownerId, String ownerType);

    boolean existsByNumber(BigInteger number);

    Optional<Account> findByNumber(BigInteger number);
}
