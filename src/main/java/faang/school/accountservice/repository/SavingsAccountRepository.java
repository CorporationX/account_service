package faang.school.accountservice.repository;

import faang.school.accountservice.model.entity.SavingsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsAccountRepository extends JpaRepository<SavingsAccount, Long> {

    @Query(value = "SELECT * FROM savings_account sa WHERE sa.account_number IN :accountNumbers", nativeQuery = true)
    List<SavingsAccount> findSaIdsByAccountNumbers(@Param("accountNumbers") List<String> accountNumbers);
}
