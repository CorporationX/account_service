package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.SavingsAccount;
import faang.school.accountservice.enums.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, UUID> {

    SavingsAccount findByAccount_OwnerIdAndAccount_OwnerType(long ownerId, OwnerType ownerType);
}
