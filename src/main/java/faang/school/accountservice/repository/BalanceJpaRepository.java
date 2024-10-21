package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
    Balance findBalanceByAccount_Id(long accountId);
}
