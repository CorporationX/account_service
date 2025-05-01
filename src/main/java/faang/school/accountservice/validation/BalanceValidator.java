package faang.school.accountservice.validation;

import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.model.Balance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BalanceValidator {

    public void authValidation(Balance balance, AccountOperation operation) {
        if (!balance.getCurrency().equals(operation.getCurrency())) {
            throw new IllegalStateException("Валюта счёта не соответствует: " + operation.getCurrency());
        }
        if (balance.getClearBalance().compareTo(operation.getAmount()) < 0) {
            throw new IllegalStateException("Недостаточно средств на счёте: " + operation.getSenderAccountId());
        }
    }

    public void clearValidation(Balance senderBalance, Balance recepientBalance, BigDecimal amount) {
        if (!senderBalance.getCurrency().equals(recepientBalance.getCurrency())) {
            throw new IllegalStateException("Не совпадает валюта счетов");
        }
        if (senderBalance.getAuthBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно зарезервированных средств for clearing" + senderBalance.getAccountId());
        }
    }

    public void cancelValidation(Balance balance, BigDecimal amount) {
        if (balance.getAuthBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Недостаточно зарезервированных средств for clearing" + balance.getAccountId());
        }
    }
}
