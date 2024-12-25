package faang.school.accountservice.repository.jpa;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.model.account.Account;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    Optional<Account> findByIdAndStatus(long id, AccountStatus accountStatus);

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("SELECT a FROM Account a WHERE a.owner IN (SELECT o FROM Owner  o WHERE o.ownerId IN :ownerIds)")
    List<Account> findAccountsByOwnerIds(@Param("ownerIds") List<Long> ownerIds);

    List<Account> findAccountsByStatus(AccountStatus accountStatus);
}
