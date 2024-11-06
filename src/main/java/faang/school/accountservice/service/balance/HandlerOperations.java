package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class HandlerOperations {

    public Balance executeOperation(TransactionDto dto, Account account) {
        Balance balance = account.getBalance();
        BigDecimal amount = dto.getAmount();
        BigDecimal actualBalance = balance.getActualBalance();
        BigDecimal authorizationBalance = balance.getAuthorizationBalance();
        long paymentNumber = dto.getPaymentNumber();

        TransactionType type = dto.getTransactionType();

        switch (type) {
            case DEBITING -> debiting(balance, amount, actualBalance, authorizationBalance, paymentNumber);
            case CREDITING -> crediting(balance, amount);
            case RETENTION -> retention(balance, amount, actualBalance, paymentNumber);
        }

        return balance;
    }

    private Balance retention(Balance balance,
                              BigDecimal amount,
                              BigDecimal actualBalance,
                              long paymentNumber) {

        if (amount.compareTo(actualBalance) > 0) {
            throw new RuntimeException("the amount cannot be more than the current balance");
        }

        BigDecimal newActualBalance = actualBalance.subtract(amount);
        balance.setActualBalance(newActualBalance);
        balance.setAuthorizationBalance(amount);
        balance.setPaymentNumber(paymentNumber);
        return balance;
    }

    private Balance debiting(Balance balance,
                             BigDecimal amount,
                             BigDecimal actualBalance,
                             BigDecimal authorizationBalance,
                             long paymentNumber) {

        if (paymentNumber == balance.getPaymentNumber()) {

            if (amount.compareTo(authorizationBalance) > 0) {
                throw new RuntimeException("the amount cannot be more than the authorization balance");
            }

            BigDecimal newAuthorizationBalance = balance.getAuthorizationBalance().subtract(amount);
            balance.setAuthorizationBalance(newAuthorizationBalance);
            return balance;
        }

        if (amount.compareTo(actualBalance) > 0) {
            throw new RuntimeException("the amount cannot be more than the current balance");
        }

        BigDecimal newActualBalance = balance.getActualBalance().subtract(amount);
        balance.setActualBalance(newActualBalance);
        return balance;
    }

    private Balance crediting(Balance balance, BigDecimal amount) {

        balance.setActualBalance(amount);
        return balance;
    }
}
