package faang.school.accountservice.jpa;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
    @Query(value = "SELECT * FROM Balance "
            , nativeQuery = true)
    Balance findBalance(@Param("acc_num") String accountNum);
}
