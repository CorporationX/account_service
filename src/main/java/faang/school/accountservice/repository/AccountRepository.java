package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.OwnerType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);

    default Account findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Account not found with id " + id));
    }

    default Account findByAccountNumberOrThrow(String accountNumber) {
        return findByAccountNumber(accountNumber)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with number " + accountNumber));
    }

    @Query(value = "SELECT a.number FROM account a WHERE a.owner_id = :ownerId", nativeQuery = true)
    List<String> findNumbersByOwnerId(@Param("ownerId") Long ownerId);

}
