package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountNumberType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, String> {

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("SELECT s FROM AccountNumbersSequence s WHERE s.type = :type")
    Optional<AccountNumbersSequence> findByTypeWithOptimisticLock(@Param("type") AccountNumberType type);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AccountNumbersSequence s WHERE s.type = :type")
    void lockForGeneration(@Param("type") AccountNumberType type);
}
