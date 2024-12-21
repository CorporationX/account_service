package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.account.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByOwnerIdAndOwner(long ownerId, Owner owner);
}
