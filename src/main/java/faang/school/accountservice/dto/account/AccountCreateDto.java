package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.InvoiceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountCreateDto {
    @Positive
    private Long userId;
    @Positive
    private Long projectId;
    @NotNull
    private InvoiceType invoiceType;
    @NotNull
    private Currency currency;
}
