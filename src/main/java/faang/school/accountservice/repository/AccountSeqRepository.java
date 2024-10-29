package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.enums.AccountType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountSeqRepository extends JpaRepository<AccountSeq, String> {

    AccountSeq findByAccountType(String accountType);

    @Transactional
    default AccountSeq createCounter(String accountType) {
        if (findByAccountType(accountType) != null) {
            return null;
        }
        AccountSeq sequence = new AccountSeq();
        sequence.setAccountType(AccountType.valueOf(accountType));
        sequence.setCounter(1L);

        return save(sequence);
    }

    @Query(nativeQuery = true, value = """
            UPDATE account_numbers_sequence SET counter = counter + :batchSize
            WHERE account_type = :type
            """)
    @Modifying
    void incrementCounter(String type, int batchSize);

}
