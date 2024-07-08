package faang.school.accountservice.repository;

import faang.school.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query(nativeQuery = true, value = """
            select case when count(*) = 0 then true else false end from account a
            where a.number = :payment_number
            """)
    boolean checkNumberIsUnique(String payment_number);
}
