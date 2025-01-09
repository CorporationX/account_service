package faang.school.accountservice.repository;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.cashback.Operation;
import feign.Param;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.owner IN (SELECT o FROM Owner  o WHERE o.ownerId IN :ownerIds)")
    List<Account> findAccountsByOwnerIds(@Param("ownerIds") List<Long> ownerIds);

    Optional<Account> findById(long id);

    Optional<Account> findByIdAndStatus(long id, AccountStatus accountStatus);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAll(Specification specification);

    List<Account> findAccountsByStatus(AccountStatus accountStatus);

    Optional<Account> getAccountByIdAndStatus(Long id, AccountStatus accountStatus);

    Account getAccountById(Long id);

    Account getAccountByAccountNumber(@Pattern(regexp = "^[0-9]{12,20}$", message = "Account number must be between 12 and 20 digits and contain only numbers.") String accountNumber);

    @Query(value = """
            SELECT a.account_number
            FROM account a
            WHERE has_cashback_tariff = true
            LIMIT :batchSize
            """,
            nativeQuery = true
    )
    List<Account> findAllWithCashbackTariff(PageRequest pageRequest);
}