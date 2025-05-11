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

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AccountNumbersSequence s WHERE s.type = :type")
    Optional<AccountNumbersSequence> findByTypeForUpdate(@Param("type") AccountNumberType type);
}
