package faang.school.accountservice.entity.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import faang.school.accountservice.enums.account.AccountEnum;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, AccountEnum> {

    @Modifying
    @Transactional
    @Query("UPDATE AccountNumbersSequence ans SET ans.currentCounter = ans.currentCounter + 1 WHERE ans.accountType = :accountType AND ans.currentCounter = :currentCounter")
    int incrementCounterIfEquals(AccountEnum accountType, Long currentCounter);

    default AccountNumbersSequence createCounter(AccountEnum accountType) {
        AccountNumbersSequence sequence = new AccountNumbersSequence();
        sequence.setAccountType(accountType);
        sequence.setCurrentCounter(0L);
        return save(sequence);
    }
}
