package faang.school.accountservice.repository;

import faang.school.accountservice.model.Owner;
import faang.school.accountservice.model.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByAccountIdAndOwnerType(long accountId, OwnerType ownerType);
}