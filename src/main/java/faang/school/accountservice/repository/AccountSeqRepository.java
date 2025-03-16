package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountSeq;
import faang.school.accountservice.entity.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountSeqRepository extends JpaRepository<AccountSeq, String> {

    Optional<AccountSeq> findByType(AccountType type);

    @Query(nativeQuery = true, value = """
        UPDATE account_number_sequence
        SET counter = counter + :batchSize
        WHERE type = :type
        RETURNING type, counter, version
    """)
    AccountSeq incrementCounter(@Param("type") String type, @Param("batchSize") int batchSize);
}

