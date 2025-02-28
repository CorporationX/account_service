package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsAccountByAccountNumber(String number);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);

    default Account findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new NoSuchElementException("Account not found with id " + id));
    }

    default Account findByAccountNumberOrThrow(String accountNumber) {
        return findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NoSuchElementException("Account not found with number " + accountNumber));
    }

    @Query(value = "SELECT nextval('account_number_seq')", nativeQuery = true)
    Long getNextAccountNumber();
}
