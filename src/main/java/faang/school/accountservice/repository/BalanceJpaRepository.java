package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
    @Override
    Optional<Balance> findById(Long aLong);

    @EntityGraph(attributePaths = {"account"})
    @Query("SELECT b FROM Balance b WHERE b.account.id = :accountId")
    Optional<Balance> findByWithAccountAccountId(Long accountId);

    @Query("SELECT b FROM Balance b WHERE b.account.id = :accountId")
    Optional<Balance> findByAccountId(Long accountId);
}
