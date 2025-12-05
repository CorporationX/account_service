package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByNumber(String number);

    Page<Account> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType, Pageable pageable);

    Page<Account> findByOwnerIdAndOwnerTypeAndStatus(Long ownerId, OwnerType ownerType, AccountStatus status, Pageable pageable);

    Page<Account> findByOwnerIdAndOwnerTypeAndCurrencyAndStatus(
            Long ownerId,
            OwnerType ownerType,
            Currency currency,
            AccountStatus status,
            Pageable pageable
    );

    boolean existsByNumber(String number);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithLock(@Param("id") Long id);

    long countByOwnerIdAndOwnerTypeAndStatus(Long ownerId, OwnerType ownerType, AccountStatus status);
}
