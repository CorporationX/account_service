package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {

    Balance findByAccountId(Long accountId);

    @Query(nativeQuery = true, value = """
        UPDATE balance
        SET curr_balance = ?2
        WHERE balance.account_id = ?1
    """)
    void updateByAccountId(Long accountId, BigInteger newBalance);

}
