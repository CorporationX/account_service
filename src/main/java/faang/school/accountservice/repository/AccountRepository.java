package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);

    List<Account> findByOwnerIdAndOwnerTypeAndCurrency(
            Long ownerId,
            OwnerType ownerType,
            Currency currency
    );

    @Query("SELECT COUNT(a) FROM Account a WHERE a.ownerId = :ownerId "
            + "AND a.ownerType = :ownerType AND a.status = 'ACTIVE'")
    long countActiveAccountsByOwner(
            @Param("ownerId") Long ownerId,
            @Param("ownerType") OwnerType ownerType
    );

    boolean existsByOwnerIdAndOwnerTypeAndCurrencyAndStatus(Long ownerId, OwnerType ownerType,
                                                            Currency currency, AccountStatus accountStatus);
}