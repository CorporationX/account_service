package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountId;
import faang.school.accountservice.entity.FreeAccountNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeAccountNumbersRepository extends JpaRepository<FreeAccountNumber, FreeAccountId> {

	@Query(nativeQuery = true,
			value = """
					DELETE FROM free_account_numbers
					WHERE type = :type
					AND account_number IN (
					    SELECT  account_number
					    FROM free_account_numbers
					    WHERE type = :type
						ORDER BY account_number
					    LIMIT 1
					)
					RETURNING account_number
					""")
	@Modifying
	List<Long> pollFirst(String type);
}