package faang.school.accountservice.dto.balance;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    @Positive
    private long paymentNumber;

    @NotNull
    private TransactionType transactionType;

    @Positive
    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;
}
