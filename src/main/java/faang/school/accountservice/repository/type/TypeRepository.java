package faang.school.accountservice.repository.type;

import faang.school.accountservice.entity.type.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TypeRepository extends JpaRepository<AccountType, Integer> {

    Optional<AccountType> findByName(String name);
}
