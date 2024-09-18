package faang.school.accountservice.repository;

import faang.school.accountservice.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {

    @Query(value = "insert into balance (id ,authorized_balance, actual_balance, currency, account_id) " +
            "VALUES (gen_random_uuid(), ?1, ?2, ?3, ?4) returning *", nativeQuery = true)
    Balance create(BigDecimal authorizedBalance, BigDecimal actualBalance, String currency, Long accountId);

    @Query(name = "select * from balance where balance.account_id = :accountId")
    Optional<Balance> getByAccountId(Long accountId);
}
