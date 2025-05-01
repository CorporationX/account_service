package faang.school.accountservice.repository;

import faang.school.accountservice.model.Account;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByOwnerTypeAndOwnerId(OwnerType ownerType, Long ownerId);

    boolean existsByAccountNumber(String accountNumber);
}