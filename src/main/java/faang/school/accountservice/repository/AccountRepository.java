package faang.school.accountservice.repository;

import faang.school.accountservice.model.OwnerType;
import faang.school.accountservice.model.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByNumber(String number);

    List<Account> findAllByOwnerIdAndOwner(long ownerId, OwnerType owner);

    boolean existsByNumber(String number);
}