package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountOwnerRepository extends JpaRepository<AccountOwner, Long> {

    @Query(nativeQuery = true, value = """
        WITH is_exist AS (
            SELECT * FROM owner_account
                     WHERE owner_id = :accountOwnerId
                     AND owner_type = :ownerType
        )
        INSERT INTO owner_account (owner_id, owner_type)
        SELECT :accountOwnerId, :ownerType
        WHERE NOT EXISTS (SELECT 1 FROM is_exist)
        RETURNING *
""")
    AccountOwner getAndCreateIfNecessary(long accountOwnerId, String ownerType);
}