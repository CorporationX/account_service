package faang.school.accountservice.repository;

import faang.school.accountservice.entity.FreeAccountNumber;
import faang.school.accountservice.entity.FreeAccountNumberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface FreeAccountNumberRepository extends JpaRepository<FreeAccountNumber, FreeAccountNumberId> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = """
                    DELETE FROM free_account_numbers
                    WHERE invoice_type =:invoiceType
                    AND account_number = (
                      SELECT account_number
                      FROM free_account_numbers
                      WHERE invoice_type = :invoiceType
                      ORDER BY account_number
                      LIMIT 1
                    )
                    RETURNING invoice_type, account_number;
                    """)
    Optional<FreeAccountNumberId> getAndDeleteFirstFreeAccountNumber(@Param("invoiceType") String invoiceType);
}
