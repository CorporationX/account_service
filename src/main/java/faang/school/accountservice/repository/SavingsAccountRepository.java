package faang.school.accountservice.repository;

import faang.school.accountservice.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    @Query("SELECT s FROM SavingsAccount s INNER JOIN s.account a WHERE a.number =: number")
    Optional<SavingsAccount> findSavingsAccountByAccountNumber(@Param("number") String number);
}