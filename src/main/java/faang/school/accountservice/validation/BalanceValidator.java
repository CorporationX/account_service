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
                    "Account currency " + balance.getCurrency() +
                            " doesn't match operation currency " + operation.getCurrency()
            );
        }
        if (balance.getClearBalance().compareTo(operation.getAmount()) < 0) {
            throw new BalanceValidationException(
                    "Insufficient funds in account " + operation.getSenderAccountId() +
                            ". Available: " + balance.getClearBalance() +
                            ", Required: " + operation.getAmount()
            );
        }
    }

    public void clearValidation(Balance senderBalance, Balance recipientBalance, BigDecimal amount) {
        if (!senderBalance.getCurrency().equals(recipientBalance.getCurrency())) {
            throw new BalanceValidationException(
                    "Currency mismatch between accounts. Sender: " + senderBalance.getCurrency() +
                            ", Recipient: " + recipientBalance.getCurrency()
            );
        }
        if (senderBalance.getAuthBalance().compareTo(amount) < 0) {
            throw new BalanceValidationException(
                    "Insufficient reserved funds in account " + senderBalance.getAccountId() +
                            " for clearing. Available: " + senderBalance.getAuthBalance() +
                            ", Required: " + amount
            );
        }
    }

    public void cancelValidation(Balance balance, BigDecimal amount) {
        if (balance.getAuthBalance().compareTo(amount) < 0) {
            throw new BalanceValidationException(
                    "Insufficient reserved funds in account " + balance.getAccountId() +
                            " for cancellation. Available: " + balance.getAuthBalance() +
                            ", Required: " + amount
            );
        }
    }
}
