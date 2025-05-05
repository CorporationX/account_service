package faang.school.accountservice.repository;

import faang.school.accountservice.model.Account;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Page<Account> findByOwnerTypeAndOwnerId(OwnerType ownerType, Long ownerId, Pageable pageable);

    boolean existsByAccountNumber(String accountNumber);
}