package faang.school.accountservice.repository;

import faang.school.accountservice.model.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber,
        FreeAccountNumber.FreeAccountNumberKey> {

    @Query(value = """
            delete from free_account_numbers where 
            account_type = :accountType 
            and account_number = (
            select account_number from free_account_numbers
            where account_type = :accountType
            order by account_number
            limit 1
            ) 
            returning account_type, account_number;
            """, nativeQuery = true)
    Optional<FreeAccountNumber> getFreeAccountNumber(@Param("accountType") String accountType);

    @Query(value = """
            select count(*) from free_account_numbers
            where account_type = :accountType
            """, nativeQuery = true)
    BigInteger getFreeAccountNumbersCountByType(@Param("accountType") String accountType);
}