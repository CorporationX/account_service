package faang.school.accountservice.repository;

import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.OwnerType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType, Pageable pageable);

    boolean existsByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);
}
