package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponseDto {
    private Long id;
    private String account;
    private BigDecimal balance;
    private OwnerType ownerType;
    private AccountType accountType;
    private Currency currency;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
}
