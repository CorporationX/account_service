package faang.school.accountservice.repository.free_account;

import faang.school.accountservice.entity.free_account.AccountNumberSequence;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, AccountType> {

    @Query(value = """
                   UPDATE account_number_sequence a 
                   SET a.last_value = a.last_value + 1 
                   WHERE a.account_type = :account_type AND 
                         a.last_value = :expectedLastValue
                   """, nativeQuery = true
    )
    @Modifying
    int incrementAccountNumberIfMatch(AccountType accountType, long expectedLastValue);

    @Query("SELECT a.lastValue FROM AccountNumberSequence a WHERE a.accountType = :accountType")
    Long findLastValueByAccountType(AccountType accountType);
}
