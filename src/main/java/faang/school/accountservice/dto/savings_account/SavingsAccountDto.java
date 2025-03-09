package faang.school.accountservice.dto.savings_account;

import faang.school.accountservice.model.savings_account.SavingsAccount;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link SavingsAccount}
 */
public record SavingsAccountDto(Long id,
                                Long accountId,
                                BigDecimal balance,
                                BigDecimal actualRate,
                                LocalDateTime lastInterestDate) {
}