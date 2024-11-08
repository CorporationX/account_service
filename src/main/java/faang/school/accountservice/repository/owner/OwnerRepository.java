package faang.school.accountservice.repository.owner;

import faang.school.accountservice.entity.owner.Owner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Integer> {

    Optional<Owner> findByName(String name);

    int countAccounts();
}
