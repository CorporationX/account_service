package faang.school.accountservice.validation;

import faang.school.accountservice.exception.BalanceValidationException;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.model.Balance;
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
public class BalanceValidator {

    public void authValidation(Balance balance, AccountOperation operation) {
        if (!balance.getCurrency().equals(operation.getCurrency())) {
            throw new BalanceValidationException(
                    String.format(
                            "Account currency %s doesn't match operation currency %s",
                            balance.getCurrency(),
                            operation.getCurrency()
                    )
            );
        }
        if (balance.getClearBalance().compareTo(operation.getAmount()) < 0) {
            throw new BalanceValidationException(
                    String.format(
                            "Insufficient funds in account %s. Available: %s, Required: %s",
                            operation.getSenderAccountId(),
                            balance.getClearBalance(),
                            operation.getAmount()
                    )
            );
        }
    }

    public void clearValidation(Balance senderBalance, Balance recipientBalance, BigDecimal amount) {
        if (!senderBalance.getCurrency().equals(recipientBalance.getCurrency())) {
            throw new BalanceValidationException(
                    String.format(
                            "Currency mismatch between accounts. Sender: %s, Recipient: %s",
                            senderBalance.getCurrency(),
                            recipientBalance.getCurrency()
                    )
            );
        }
        if (senderBalance.getAuthBalance().compareTo(amount) < 0) {
            throw new BalanceValidationException(
                    String.format(
                            "Insufficient reserved funds in account %s for clearing. Available: %s, Required: %s",
                            senderBalance.getAccountId(),
                            senderBalance.getAuthBalance(),
                            amount
                    )
            );
        }
    }

    public void cancelValidation(Balance balance, BigDecimal amount) {
        if (balance.getAuthBalance().compareTo(amount) < 0) {
            throw new BalanceValidationException(
                    String.format(
                            "Insufficient reserved funds in account %s for cancellation. Available: %s, Required: %s",
                            balance.getAccountId(),
                            balance.getAuthBalance(),
                            amount
                    )
            );
        }
    }
}
