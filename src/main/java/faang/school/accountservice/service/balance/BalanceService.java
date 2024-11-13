package faang.school.accountservice.service.balance;

import faang.school.accountservice.annotation.publisher.PublishPayment;
import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.dto.payment.response.AuthPaymentResponse;
import faang.school.accountservice.dto.payment.response.CancelPaymentResponse;
import faang.school.accountservice.dto.payment.response.ClearingPaymentResponse;
import faang.school.accountservice.dto.payment.response.ErrorPaymentResponse;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.auth.payment.AuthPaymentStatus;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.exception.auth.payment.AuthPaymentHasBeenUpdatedException;
import faang.school.accountservice.exception.balance.BalanceHasBeenUpdatedException;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static faang.school.accountservice.entity.auth.payment.AuthPaymentBuilder.build;
import static faang.school.accountservice.entity.balance.BalanceBuilder.build;
import static faang.school.accountservice.enums.auth.payment.AuthPaymentStatus.CLOSED;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final AuthPaymentRepository authPaymentRepository;
    private final BalanceValidator balanceValidator;

    @SuppressWarnings("UnusedReturnValue")
    @Transactional
    public Balance createBalance(Account account) {
        Balance newBalance = build(account);

        return saveBalance(newBalance);
    }

    @Transactional
    public Balance createOrGetBalanceWithAmount(Account account, BigDecimal amount) {
        Optional<Balance> balanceOpt = balanceRepository.findBalanceByAccountId(account.getId());

        Balance balance;
        if (balanceOpt.isEmpty()) {
            balance = build(account, amount);
        } else {
            balance = balanceOpt.get();
            balance.setCurrentBalance(amount);
        }

        return saveBalance(balance);
    }

    @SuppressWarnings("UnusedReturnValue")
    @PublishPayment(returnedType = AuthPaymentResponse.class)
    @Transactional
    public AuthPayment authorizePayment(AuthPaymentRequest request) {
        Balance sourceBalance = findByAccountId(request.getSourceAccountId());
        Balance targetBalance = findByAccountId(request.getTargetAccountId());
        Money money = new Money(request.getAmount(), request.getCurrency());

        balanceValidator.checkFreeAmount(request.getOperationId(), sourceBalance, money);

        BigDecimal newSourceAuthBalance = sourceBalance.getAuthBalance().add(money.amount());
        sourceBalance.setAuthBalance(newSourceAuthBalance);
        BigDecimal newSourceCurrentBalance = sourceBalance.getCurrentBalance().subtract(money.amount());
        sourceBalance.setCurrentBalance(newSourceCurrentBalance);

        AuthPayment payment = build(request.getOperationId(), sourceBalance, targetBalance, money.amount(),
                request.getCategory());

        saveBalance(sourceBalance);
        return saveAuthPayment(payment);
    }

    @SuppressWarnings("UnusedReturnValue")
    @PublishPayment(returnedType = ClearingPaymentResponse.class)
    @Transactional
    public synchronized AuthPayment clearingPayment(ClearingPaymentRequest request) {
        AuthPayment payment = findAuthPaymentBiId(request.getOperationId());
        Balance sourceBalance = payment.getSourceBalance();
        Balance targetBalance = payment.getTargetBalance();

        balanceValidator.checkAuthPaymentForAccept(payment);

        BigDecimal newSourceAuthBalance = sourceBalance.getAuthBalance().subtract(payment.getAmount());
        sourceBalance.setAuthBalance(newSourceAuthBalance);
        BigDecimal newTargetCurrentBalance = targetBalance.getCurrentBalance().add(payment.getAmount());
        targetBalance.setCurrentBalance(newTargetCurrentBalance);

        payment.setStatus(CLOSED);

        saveBalance(sourceBalance);
        saveBalance(targetBalance);
        return saveAuthPayment(payment);
    }

    @SuppressWarnings("UnusedReturnValue")
    @PublishPayment(returnedType = CancelPaymentResponse.class)
    @Transactional
    public AuthPayment cancelPayment(CancelPaymentRequest request) {
        return rejectPayment(request.getOperationId());
    }

    @SuppressWarnings("UnusedReturnValue")
    @PublishPayment(returnedType = ErrorPaymentResponse.class)
    @Transactional
    public AuthPayment errorPayment(ErrorPaymentRequest request) {
        return rejectPayment(request.getOperationId());
    }

    private AuthPayment rejectPayment(UUID authPaymentId) {
        AuthPayment payment = authPaymentRepository.findById(authPaymentId).orElseThrow(() ->
                new ResourceNotFoundException(AuthPayment.class, authPaymentId));
        Balance sourceBalance = payment.getSourceBalance();

        balanceValidator.checkAuthPaymentForReject(payment);

        BigDecimal newSourceAuthBalance = sourceBalance.getAuthBalance().subtract(payment.getAmount());
        sourceBalance.setAuthBalance(newSourceAuthBalance);
        BigDecimal newSourceCurrentBalance = sourceBalance.getCurrentBalance().add(payment.getAmount());
        sourceBalance.setCurrentBalance(newSourceCurrentBalance);

        payment.setStatus(AuthPaymentStatus.REJECTED);

        saveBalance(sourceBalance);
        return saveAuthPayment(payment);
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional
    public Balance topUpCurrentBalance(UUID balanceId, Money money) {
        Balance balance = findById(balanceId);
        BigDecimal currentBalance = balance.getCurrentBalance();
        balance.setCurrentBalance(currentBalance.add(money.amount()));
        return saveBalance(balance);
    }

    @SuppressWarnings("UnusedReturnValue")
    @Transactional(propagation = Propagation.MANDATORY)
    public void multiplyCurrentBalance(UUID accountId, double value) {
        Balance balance = balanceRepository.findByAccount_IdWithLock(accountId).orElseThrow(() ->
                new ResourceNotFoundException(Balance.class, accountId));

        BigDecimal currentBalance = balance.getCurrentBalance();

        BigDecimal multiplier = BigDecimal.valueOf(value);
        BigDecimal newCurrentBalance = currentBalance.add(currentBalance.multiply(multiplier));
        balance.setCurrentBalance(newCurrentBalance);

        persistBalance(balance);
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
                new ResourceNotFoundException(Balance.class, accountId));
    }

    @Transactional
    @Retryable(retryFor = {BalanceHasBeenUpdatedException.class})
    public void saveCashback(Balance balance, BigDecimal cashback) {
        Balance balanceDB = findById(balance.getId());

        BigDecimal currentBalance = balanceDB.getCurrentBalance();
        balanceDB.setCurrentBalance(currentBalance.add(cashback));
        balanceDB.setUpdatedAt(LocalDateTime.now());

        saveBalance(balanceDB);
    }

    private Balance saveBalance(Balance balance) {
        try {
            balance = balanceRepository.saveAndFlush(balance);
        } catch (OptimisticLockingFailureException exception) {
            throw new BalanceHasBeenUpdatedException(balance.getId());
        }
        return balance;
    }

    private Balance persistBalance(Balance balance) {
        return balanceRepository.save(balance);
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
