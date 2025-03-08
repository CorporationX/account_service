package faang.school.accountservice.repository;

import faang.school.accountservice.entity.AccountNumberSequence;
import faang.school.accountservice.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, Long> {

    Optional<AccountNumberSequence> findByInvoiceType(InvoiceType invoiceType);

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = """
                    UPDATE account_numbers_sequence
                    SET current_counter = current_counter + 1
                    WHERE invoice_type =:invoiceType
                    AND current_counter = :expectedValue
                    RETURNING current_counter
                    """)
    Optional<Long> incrementCounter(@Param("invoiceType") String invoiceType,
                                    @Param("expectedValue") long expectedValue);
}
