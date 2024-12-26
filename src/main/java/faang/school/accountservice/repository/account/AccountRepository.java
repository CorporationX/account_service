package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByOwnerIdAndOwnerType(long ownerId, OwnerType ownerType);
}
