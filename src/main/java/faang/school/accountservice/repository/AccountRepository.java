package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {

    boolean existsByAccountNumberAndOwnerIdAndOwnerTypeAndType(String accountNumber, BigInteger ownerId,
                                                               AccountOwnerType ownerType, AccountType type);

}
