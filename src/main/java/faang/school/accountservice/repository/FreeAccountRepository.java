package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccountRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

}
