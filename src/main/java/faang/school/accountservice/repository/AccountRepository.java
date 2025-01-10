package faang.school.accountservice.repository;

import faang.school.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.SQL;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByNumber(String number);

    @Query(
            "SELECT s FROM Account s WHERE s.number = :number"
    )
    Account findByNumber(@Param("number") String number);
}
