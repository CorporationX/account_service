package faang.school.accountservice.repository;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    boolean existsAccountByAccountNumber(String accountNumber);
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(nativeQuery = true, value = """
            select
                a.*
            from accounts a
            inner join accounts_owners ao on a.owner_id = ao.id
            where ao.owner_id = ?1
                and a.currency = ?2
                and a.status = 0
            """
    )
    Optional<Account> findCurrencyAccountByOwner(long ownerId, long currencyNumber);

}
