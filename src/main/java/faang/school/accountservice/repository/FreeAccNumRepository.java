package faang.school.accountservice.repository;

import faang.school.accountservice.Entity.FreeAccountNumber;
import faang.school.accountservice.model.AccTypeFreeNumberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FreeAccNumRepository extends JpaRepository<FreeAccountNumber, AccTypeFreeNumberId> {
    @Query(nativeQuery = true, value = """
            delete from free_accounts_numbers where account_type =:type and free_number = (
                select free_number from free_accounts_numbers where account_type =:type  limit 1
            )
            returning free_number,account_type;
            """)
    FreeAccountNumber retrieveFreeAccNum(String type);
}
