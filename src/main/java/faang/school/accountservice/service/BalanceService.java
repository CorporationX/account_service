package faang.school.accountservice.service;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.AuthorizationStatus;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAuthPayment;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuthPaymentRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final BalanceAuthPaymentRepository balanceAuthPaymentRepository;

    @Transactional
    public Balance createBalance() {
        Balance balance = Balance.builder()
                .build();

        return saveBalance(balance);
    }

    @Transactional
    public BalanceAuthPayment createAuthPayment(UUID balanceId, Money money) {
        validateCurrency(money.currency());
        validatePositiveSum(money.amount());

        Balance balance = balanceRepository.findById(balanceId).orElseThrow();
        validateEnoughMoney(balance, money.amount());

        BalanceAuthPayment authPayment = BalanceAuthPayment.builder()
                .balance(balance)
                .amount(money.amount())
                .status(AuthorizationStatus.ACTIVATED)
                .build();

        balance.setAuthorization(balance.getAuthorization().add(money.amount()));
        saveBalance(balance);

        return saveBalanceAuthPayment(authPayment);
    }

    @Transactional
    public BalanceAuthPayment rejectAuthPayment(UUID authPaymentId) {
        BalanceAuthPayment payment = balanceAuthPaymentRepository.findById(authPaymentId).orElseThrow();

        Balance balance = payment.getBalance();
        BigDecimal newAuthBalance = balance.getAuthorization().subtract(payment.getAmount());
        BigDecimal actualBalance = balance.getActual().add(payment.getAmount());
        balance.setAuthorization(newAuthBalance);
        balance.setActual(actualBalance);
        payment.setStatus(AuthorizationStatus.REJECTED);
        saveBalance(balance);

        return saveBalanceAuthPayment(payment);
    }


    @Transactional
    public Balance topUpCurrentBalance(UUID balanceId, Money money) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow();

        validateCurrency(money.currency());
        validatePositiveSum(money.amount());

        BigDecimal currentBalance = balance.getActual();
        balance.setActual(currentBalance.add(money.amount()));
        return saveBalance(balance);
    }

    @Transactional(readOnly = true)
    public Balance getBalance(UUID accountUuid) {
        Account account = accountRepository.findById(accountUuid)
                .orElseThrow();

        return account.getBalance();
    }

    @Transactional
    public BalanceAuthPayment completeAuthorizationWriteOff(UUID authPaymentId) {
        BalanceAuthPayment authPayment = balanceAuthPaymentRepository.findById(authPaymentId)
                .orElseThrow(() -> new ValidationException("Authorization payment not found"));
        log.error("Authorization payment {} not found", authPaymentId);

        validatePaymentIsStatus(authPayment);
        validateActualBalance_ToBeWrittenOff(authPayment);

        Balance balance = authPayment.getBalance();

        balance.setActual(balance.getActual().subtract(authPayment.getAmount()));
        balance.setAuthorization(balance.getAuthorization().subtract(authPayment.getAmount()));

        authPayment.setStatus(AuthorizationStatus.CONFIRMED);
        saveBalance(balance);
        return saveBalanceAuthPayment(authPayment);
    }

    private void validatePaymentIsStatus(BalanceAuthPayment authPayment) {
        if(authPayment.getStatus() != AuthorizationStatus.ACTIVATED) {
            throw new ValidationException("The payment is not in an authorized state and cannot be processed");
        }
    }

    private void validateActualBalance_ToBeWrittenOff(BalanceAuthPayment authPayment) {
        Balance balance = authPayment.getBalance();
        if(balance.getActual().compareTo(authPayment.getAmount()) < 0) {
            throw new ValidationException("Insufficient funds for clearing the payment");
        }
    }

    private void validateCurrency(Currency currency) {
        if (!currency.equals(Currency.RUB)) {
            throw new ValidationException(String.format("Our bank accepts only rubles. Your currency is  %s", currency));
        }
    }

    private void validatePositiveSum(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException(String.format("The amount of money must be positive. Your sum is  %s", amount));
        }
    }

    private void validateEnoughMoney(Balance balance, BigDecimal amount) {
        BigDecimal currentBalanceAmount = balance.getActual().subtract(balance.getAuthorization());
        if (amount.compareTo(currentBalanceAmount) > 0) {
            throw new ValidationException(String.format("Not enough money for authorization %s", amount));
        }
    }

    private Balance saveBalance(Balance balance) {
        try {
            balance = balanceRepository.save(balance);
            balanceRepository.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new RuntimeException(String.format("Error authorization payment %s", balance.getId()));
        }
        return balance;
    }

    private BalanceAuthPayment saveBalanceAuthPayment(BalanceAuthPayment balanceAuthPayment) {
        try {
            balanceAuthPayment = balanceAuthPaymentRepository.save(balanceAuthPayment);
            balanceAuthPaymentRepository.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new RuntimeException(String.format("Error authorization payment %s", balanceAuthPayment.getId()));
        }
        return balanceAuthPayment;
    }
}
