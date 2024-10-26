package faang.school.accountservice.repository;


import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.owner.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("""
        SELECT a FROM Account a
        WHERE a.owner.externalId = :externalId
        AND a.owner.type = :type
        """)
    List<Account> findByOwner(Long externalId, OwnerType type);
}
