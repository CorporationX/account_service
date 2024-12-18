package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountOwnerRepository extends JpaRepository<AccountOwner, Long> {

    Optional<AccountOwner> findByOwnerIdAndOwnerType(Long ownerId, OwnerType ownerType);
}
