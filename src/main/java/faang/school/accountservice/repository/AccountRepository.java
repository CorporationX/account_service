package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByNumber(String number);

    @Query(nativeQuery = true, value = """
            SELECT * FROM account
            WHERE owner_id = :ownerId AND owner_type = :ownerType
            """)
    Optional<Account> findByOwner(Long ownerId, OwnerType ownerType);
}
