package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Currency;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    @Query(nativeQuery = true, value = """
            SELECT * FROM account WHERE number = ?1
            """)
    Optional<Account> findByNumber(String number);

    Optional<Account> findByOwnerIdAndCurrency(Long ownerId, Currency currency);
}
