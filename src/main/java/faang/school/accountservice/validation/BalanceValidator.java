package faang.school.accountservice.validation;

import faang.school.accountservice.exception.InsufficientFundsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
public class BalanceValidator {
    private static final String ACTUAL_BALANCE_ERROR =
            "Insufficient account balance. Available: %s, Required: %s";
    private static final String AUTHORIZED_BALANCE_ERROR =
            "Insufficient reserved funds. Available: %s, Required: %s";

    public void validateActualBalanceSufficiency(BigDecimal actualBalance, BigDecimal amount) {
        validateBalance(actualBalance, amount, ACTUAL_BALANCE_ERROR);
    }

    public void validateAuthorizedBalanceSufficiency(BigDecimal authorizedBalance, BigDecimal amount) {
        validateBalance(authorizedBalance, amount, AUTHORIZED_BALANCE_ERROR);
    }

    private void validateBalance(BigDecimal balance, BigDecimal amount, String errorTemplate) {
        if (balance.compareTo(amount) < 0) {
            String error = String.format(errorTemplate, balance, amount);
            log.warn(error);
            throw new InsufficientFundsException(error);
        }
    }
}
