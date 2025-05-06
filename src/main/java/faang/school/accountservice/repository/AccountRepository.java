package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);
}
