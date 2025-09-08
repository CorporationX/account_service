package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.OwnerType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM account
            WHERE  ownerType = :ownerType
            AND ownerId = :ownerId
            """)
    List<Account> findAllByOwnerTypeAndId(OwnerType ownerType, Long ownerId);

    default Account findByIdOrThrow(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

}
