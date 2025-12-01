package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumbersSequence;
import faang.school.accountservice.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountNumbersSequenceRepository extends JpaRepository<AccountNumbersSequence, AccountType> {

	@Query(nativeQuery = true, value = """
			UPDATE account_number_sequence
			SET counter = counter + :batchSize
			WHERE type = :type
			RETURNING counter
			""")
	@Modifying
	List<Long> incrementCounter(String type, int batchSize);
}
