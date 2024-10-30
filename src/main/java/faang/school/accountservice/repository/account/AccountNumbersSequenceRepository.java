package faang.school.accountservice.repository.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import faang.school.accountservice.entity.account.AccountNumbersSequence;
import faang.school.accountservice.enums.account.AccountEnum;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, AccountEnum> {

    @Modifying
    @Query(
        """
        UPDATE AccountNumbersSequence ans
        SET ans.currentCounter = ans.currentCounter + 1
        WHERE ans.accountType = :accountType
        AND ans.currentCounter = :currentCounter
        """
    )
    int incrementCounterForAccountTypeInternal(AccountEnum accountType, Long currentCounter);

    default boolean incrementCounterForAccountType(AccountEnum accountType, Long currentCounter) {
        return incrementCounterForAccountTypeInternal(accountType, currentCounter) == 1;
    }

    default AccountNumbersSequence createCounterForAccountType(AccountEnum accountType) {
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setAccountType(accountType);
        sequence.setCurrentCounter(0L);
        return save(sequence);
    }
}
