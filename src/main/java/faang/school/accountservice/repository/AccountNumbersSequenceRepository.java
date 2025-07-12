package faang.school.accountservice.repository;

import faang.school.accountservice.model.AccountBalanceType;
import faang.school.accountservice.model.AccountNumberSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {
    @Modifying
    @Transactional
    @Query(value = "UPDATE AccountNumberSequence SET acountsCount = acountsCount + 1 " +
            "WHERE accountBalanceType = :balanceType")
    void incrementCountByBalanceType(@Param("balanceType") AccountBalanceType balanceType);

    @Modifying
    @Transactional
    @Query(value = "UPDATE AccountNumberSequence SET acountsCount = acountsCount + :quantity " +
            "WHERE accountBalanceType = :balanceType")
    void incrementCountByTypeWithQuantity(@Param("balanceType") AccountBalanceType balanceType,
                                          @Param("quantity") Integer quantity);

    @Transactional
    @Query(value = "SELECT acountsCount FROM AccountNumberSequence WHERE accountBalanceType = :balanceType")
    Integer getIncrementedCountByBalanceType(@Param("balanceType") AccountBalanceType balanceType);
}
