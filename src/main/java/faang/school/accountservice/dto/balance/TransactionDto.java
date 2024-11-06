package faang.school.accountservice.dto.balance;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.TransactionType;
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

    long account;
    long paymentNumber;
    TransactionType transactionType;
    BigDecimal amount;
    Currency currency;
}
