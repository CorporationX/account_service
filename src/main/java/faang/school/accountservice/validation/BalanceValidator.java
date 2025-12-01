package faang.school.accountservice.validation;

import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.exception.InsufficientBalanceException;
import faang.school.accountservice.exception.InvalidBalanceOperationException;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceValidator {
    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;

    public Balance validateBalanceExisting(long balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> {
                    String errorMessage = String.format("Balance %d not found",
                            balanceId);
                    log.error(errorMessage);
                    return new BalanceNotFoundException(errorMessage);
                });
    }

    public Account validateAccountExisting(long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> {
            String errorMessage = String.format("Account %d not found",
                    accountId);
            log.error(errorMessage);
            return new AccountNotFoundException(errorMessage);
        });
    }

    public void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            String errorMessage = String.format("Amount %s must be positive",
                    amount);
            log.error(errorMessage);
            throw new InvalidBalanceOperationException(errorMessage);
        }
    }

    public void validateEnoughActualBalance(Balance balance, BigDecimal amount) {
        BigDecimal actualBalance = balance.getActualBalance();
        if (actualBalance.compareTo(amount) < 0) {
            String errorMessage = String.format("Not enough actual balance %s, required: %s",
                    actualBalance,
                    amount);
            log.error(errorMessage);
            throw new InsufficientBalanceException(errorMessage);
        }
    }

    public void validateEnoughAuthorizationBalance(Balance balance, BigDecimal amount) {
        BigDecimal authorizationBalance = balance.getAuthorizationBalance();
        if (authorizationBalance.compareTo(amount) < 0) {
            String errorMessage = String.format("Not enough authorization balance %s, required: %s",
                    authorizationBalance,
                    amount);
            log.error(errorMessage);
            throw new InsufficientBalanceException(errorMessage);

        }
    }
}
