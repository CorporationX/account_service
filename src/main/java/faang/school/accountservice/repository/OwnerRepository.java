package faang.school.accountservice.repository;

import faang.school.accountservice.model.owner.Owner;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    @Query("SELECT o FROM Owner o " +
            "WHERE o.ownerId = :ownerId AND o.type = :type")
    Optional<Owner> findOwnerByIdAndType(Long ownerId, OwnerType type);
}
