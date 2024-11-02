package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.listener.pending.OperationMessage;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.enums.auth.payment.AuthPaymentStatus;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.pending.Category;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.auth.payment.AuthPaymentHasBeenUpdatedException;
import faang.school.accountservice.exception.balance.BalanceHasBeenUpdatedException;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static faang.school.accountservice.entity.auth.payment.AuthPaymentBuilder.build;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.CLOSED;
import static faang.school.accountservice.entity.balance.BalanceBuilder.build;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final AuthPaymentRepository authPaymentRepository;
    private final BalanceValidator balanceValidator;

    @Transactional
    public Balance createBalance(Account account) {
        Balance newBalance = build(account);

        return saveBalance(newBalance);
    }

    @Transactional
    public AuthPayment authorizePayment(OperationMessage operation, Balance sourceBalance, Balance targetBalance,
                                        Money money, Category category) {
        balanceValidator.checkFreeAmount(operation, sourceBalance, money);

        AuthPayment payment = build(operation.getOperationId(), sourceBalance, targetBalance, money.amount(), category);
        sourceBalance.setAuthBalance(sourceBalance.getAuthBalance().add(money.amount()));

        saveBalance(sourceBalance);
        return saveAuthPayment(payment);
    }

    @Transactional
    public AuthPayment acceptPayment(UUID authPaymentId, Money money) {
        AuthPayment payment = findAuthPaymentBiId(authPaymentId);
        balanceValidator.checkAuthPaymentForAccept(money, payment);

        Balance sourceBalance = sourceBalanceUpdate(payment, money);
        Balance targetBalance = targetBalanceUpdate(payment, money);

        payment.setAmount(money.amount());
        payment.setStatus(CLOSED);

        saveBalance(sourceBalance);
        saveBalance(targetBalance);
        return saveAuthPayment(payment);
    }

    @Transactional
    public AuthPayment rejectPayment(UUID authPaymentId) {
        AuthPayment payment = findAuthPaymentBiId(authPaymentId);
        balanceValidator.checkAuthPaymentForReject(payment);

        Balance sourceBalance = payment.getSourceBalance();
        BigDecimal newAuthBalance = sourceBalance.getAuthBalance().subtract(payment.getAmount());
        sourceBalance.setAuthBalance(newAuthBalance);

        payment.setStatus(AuthPaymentStatus.REJECTED);

        saveBalance(sourceBalance);
        return saveAuthPayment(payment);
    }

    @Transactional
    public Balance topUpCurrentBalance(UUID balanceId, Money money) {
        Balance balance = findById(balanceId);
        BigDecimal currentBalance = balance.getCurrentBalance();
        balance.setCurrentBalance(currentBalance.add(money.amount()));
        return saveBalance(balance);
    }

    @Transactional
    public Balance multiplyCurrentBalance(UUID balanceId, Double value) {
        Balance balance = findById(balanceId);
        BigDecimal currentBalance = balance.getCurrentBalance();

        BigDecimal multiplier = BigDecimal.valueOf(value);
        BigDecimal newCurrentBalance = currentBalance.add(currentBalance.multiply(multiplier));
        balance.setCurrentBalance(newCurrentBalance);

        return saveBalance(balance);
    }

    @Transactional(readOnly = true)
    public Balance findById(UUID id) {
        return balanceRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Balance.class, id));
    }

    @Transactional(readOnly = true)
    public AuthPayment findAuthPaymentBiId(UUID id) {
        return authPaymentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(AuthPayment.class, id));
    }

    @Transactional(readOnly = true)
    public Balance findByAccountId(UUID accountId) {
        return balanceRepository.findBalanceByAccountId(accountId).orElseThrow(() ->
                new ResourceNotFoundException(Account.class, accountId));
    }

    private Balance sourceBalanceUpdate(AuthPayment payment, Money money) {
        Balance sourceBalance = payment.getSourceBalance();

        BigDecimal newCurrentBalance = sourceBalance.getCurrentBalance().subtract(money.amount());
        sourceBalance.setCurrentBalance(newCurrentBalance);
        BigDecimal newAuthBalance = sourceBalance.getAuthBalance().subtract(payment.getAmount());
        sourceBalance.setAuthBalance(newAuthBalance);

        return sourceBalance;
    }

    private Balance targetBalanceUpdate(AuthPayment payment, Money money) {
        Balance targetBalance = payment.getTargetBalance();

        BigDecimal newCurrentBalance = targetBalance.getCurrentBalance().add(money.amount());
        targetBalance.setCurrentBalance(newCurrentBalance);

        return targetBalance;
    }

    private Balance saveBalance(Balance balance) {
        try {
            balance = balanceRepository.saveAndFlush(balance);
        } catch (OptimisticLockingFailureException exception) {
            throw new BalanceHasBeenUpdatedException(balance.getId());
        }
        return balance;
    }

    private AuthPayment saveAuthPayment(AuthPayment payment) {
        try {
            payment = authPaymentRepository.saveAndFlush(payment);
        } catch (OptimisticLockingFailureException exception) {
            throw new AuthPaymentHasBeenUpdatedException(payment.getId());
        }
        return payment;
    }
}
