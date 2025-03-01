package faang.school.accountservice.repository.account;

import faang.school.accountservice.entity.account.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.Optional;

public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumber.FreeAccountNumberId> {

    @Query(nativeQuery = true, value = """
            SELECT account_number FROM free_account_numbers
            WHERE account_type = :accountType ORDER BY account_number ASC LIMIT 1""")
    Optional<BigInteger> findFirstFreeAccountNumber(String accountType);

    @Modifying
    @Query(value = "DELETE FROM FreeAccountNumber f WHERE f.id.accountNumber = :accountNumber")
    void deleteFreeAccountNumberByAccountNumber(BigInteger accountNumber);

}