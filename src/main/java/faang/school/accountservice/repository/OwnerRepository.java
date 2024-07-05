package faang.school.accountservice.repository;

import faang.school.accountservice.model.Owner;
import faang.school.accountservice.model.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    @Query("SELECT o FROM Owner o WHERE (o.custodianId = :accountId) AND o.ownerType = :ownerType")
    Optional<Owner> findByProjectOrUserIdAndOwnerType(@Param("accountId") long accountId, @Param("ownerType") OwnerType ownerType);
}
