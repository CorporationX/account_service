package faang.school.accountservice.validation.balance;

import faang.school.accountservice.exception.balance.NotEnoughAvailableFundsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@Slf4j
public class BalanceValidator {
    public void validateAmountDoesNotExceedLimit(UUID balanceId, BigDecimal amount, BigDecimal limit) {
        if (amount.compareTo(limit) > 0) {
            log.error("Not enough funds: requested {}, available {}", amount, limit);
            throw new NotEnoughAvailableFundsException(balanceId, amount, limit);
        }
    }
}
