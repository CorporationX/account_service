package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountOwner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOwnerRepository extends JpaRepository<AccountOwner, Long> {

}
