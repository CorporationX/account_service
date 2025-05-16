package faang.school.accountservice.validation;

import faang.school.accountservice.exception.BalanceValidationException;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.model.BalanceDMS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Валидатор для проверок операций с балансом счета.
 * <p>
 * Выполняет следующие проверки:
 * <ul>
 *   <li>Валидацию при авторизации операций</li>
 *   <li>Валидацию при клиринге (проведении) операций</li>
 *   <li>Валидацию при отмене операций</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class BalanceDMSValidator {

    public void authValidation(BalanceDMS balanceDMS, AccountOperation operation) {
        if (!balanceDMS.getCurrency().equals(operation.getCurrency())) {
            throw new BalanceValidationException(
                    String.format(
                            "Account currency %s doesn't match operation currency %s",
                            balanceDMS.getCurrency(),
                            operation.getCurrency()
                    )
            );
        }
        if (balanceDMS.getClearBalance().compareTo(operation.getAmount()) < 0) {
            throw new BalanceValidationException(
                    String.format(
                            "Insufficient funds in account %s. Available: %s, Required: %s",
                            operation.getSenderAccountId(),
                            balanceDMS.getClearBalance(),
                            operation.getAmount()
                    )
            );
        }
    }

    public void clearValidation(BalanceDMS senderBalanceDMS, BalanceDMS recipientBalanceDMS, BigDecimal amount) {
        if (!senderBalanceDMS.getCurrency().equals(recipientBalanceDMS.getCurrency())) {
            throw new BalanceValidationException(
                    String.format(
                            "Currency mismatch between accounts. Sender: %s, Recipient: %s",
                            senderBalanceDMS.getCurrency(),
                            recipientBalanceDMS.getCurrency()
                    )
            );
        }
        if (senderBalanceDMS.getAuthBalance().compareTo(amount) < 0) {
            throw new BalanceValidationException(
                    String.format(
                            "Insufficient reserved funds in account %s for clearing. Available: %s, Required: %s",
                            senderBalanceDMS.getAccountId(),
                            senderBalanceDMS.getAuthBalance(),
                            amount
                    )
            );
        }
    }

    public void cancelValidation(BalanceDMS balanceDMS, BigDecimal amount) {
        if (balanceDMS.getAuthBalance().compareTo(amount) < 0) {
            throw new BalanceValidationException(
                    String.format(
                            "Insufficient reserved funds in account %s for cancellation. Available: %s, Required: %s",
                            balanceDMS.getAccountId(),
                            balanceDMS.getAuthBalance(),
                            amount
                    )
            );
        }
    }
}
