package faang.school.accountservice.repository;

import faang.school.accountservice.model.Owner;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    @Query("SELECT o FROM Owner o WHERE o.ownerType = :ownerType AND o.personId = :personId")
    Optional<Owner> findByOwnerTypeAndPersonId(@Param("ownerType") OwnerType ownerType,
                                               @Param("personId") Long personId);

    default Owner upsertOwner(OwnerType ownerType, Long personId) {
        return findByOwnerTypeAndPersonId(ownerType, personId)
                .orElseGet(() -> {
                    Owner newOwner = Owner.builder()
                            .ownerType(ownerType)
                            .personId(personId)
                            .build();
                    return save(newOwner);
                });
    }
}
