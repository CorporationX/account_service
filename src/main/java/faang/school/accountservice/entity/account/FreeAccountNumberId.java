package faang.school.accountservice.entity.account;

import faang.school.accountservice.enums.InvoiceType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreeAccountNumberId implements Serializable {
    @Column(name = "invoice_type", length = 32, nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceType invoiceType;
    @Column(name = "account_number", length = 20, nullable = false)
    private String accountNumber;
}
