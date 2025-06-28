package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query(nativeQuery = true,
            value = """
                    SELECT *
                    FROM Account
                    WHERE id = :id
                    FOR UPDATE
                    """)
    Optional<Account> findByIdForUpdate(@Param("id") UUID id);
}
