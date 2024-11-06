package faang.school.accountservice.service.balance.transactionHandler;

import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.TransactionType;
import faang.school.accountservice.exception.BalanceException;

import java.math.BigDecimal;

public class RetentionHandler implements TransactionHandler {

    @Override
    public TransactionType getType() {
        return TransactionType.RETENTION;
    }

    @Override
    public Balance handle(TransactionDto transaction, Account account) {
        Balance balance = account.getBalance();
        if (transaction.getAmount().compareTo(balance.getActualBalance()) > 0) {
            throw new BalanceException("The amount cannot be more than the current balance");
        }

        BigDecimal newActualBalance = balance.getActualBalance().subtract(transaction.getAmount());
        balance.setActualBalance(newActualBalance);
        balance.setAuthorizationBalance(transaction.getAmount());
        balance.setPaymentNumber(transaction.getPaymentNumber());
        return balance;
    }
}
