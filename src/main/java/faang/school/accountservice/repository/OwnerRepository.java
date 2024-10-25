package faang.school.accountservice.repository;

import faang.school.accountservice.model.owner.Owner;
import faang.school.accountservice.model.owner.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, UUID> {

    @Query("""
            SELECT o FROM Owner o
            WHERE o.externalId = :externalId AND o.type = :type
            """)
    Optional<Owner> findOwner(Long externalId, OwnerType type);

}
