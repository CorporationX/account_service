package faang.school.accountservice.repository.account;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.model.account.AccountNumberPrefix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountNumberPrefixRepository extends JpaRepository<AccountNumberPrefix, String> {
    @Query("SELECT a.prefix FROM AccountNumberPrefix a WHERE a.type = :type")
    String findAccountNumberPrefixByType(@Param("type") AccountType type);
}
