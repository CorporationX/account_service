package faang.school.accountservice.dto.account;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Bank;
import faang.school.accountservice.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDto {
    private Long id;
    private Long userId;
    private Long projectId;
    private String accountNumber;
    private Currency currency;
    private BigDecimal amount;
    private Bank bank;
    private AccountType type;
    private AccountStatus status;
}