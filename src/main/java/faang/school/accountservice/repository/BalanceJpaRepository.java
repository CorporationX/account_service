package faang.school.accountservice.repository;

import faang.school.accountservice.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceJpaRepository extends JpaRepository<Balance, Long> {
    @Override
    Optional<Balance> findById(Long aLong);
}
