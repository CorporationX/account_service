package faang.school.accountservice.service.balance.transactionHandler;

import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.exception.BalanceException;

import java.math.BigDecimal;

public class DebitingHandler implements TransactionHandler {

    @Override
    public TransactionType getType() {
        return TransactionType.DEBITING;
    }

    @Override
    public Balance handle(TransactionDto transaction, Account account) {
        Balance balance = account.getBalance();
        BigDecimal amount = transaction.getAmount();

        if (transaction.getPaymentNumber() == balance.getPaymentNumber()) {

            if (amount.compareTo(balance.getAuthorizationBalance()) > 0) {
                throw new BalanceException("The amount cannot be more than the authorization balance");
            }

            BigDecimal newAuthorizationBalance = balance.getAuthorizationBalance().subtract(amount);
            balance.setAuthorizationBalance(newAuthorizationBalance);
            balance.setPaymentNumber(transaction.getPaymentNumber());
            return balance;
        }

        if (amount.compareTo(balance.getActualBalance()) > 0) {
            throw new BalanceException("The amount cannot be more than the current balance");
        }

        BigDecimal newActualBalance = balance.getActualBalance().subtract(amount);
        balance.setActualBalance(newActualBalance);
        balance.setPaymentNumber(transaction.getPaymentNumber());
        return balance;
    }
}
