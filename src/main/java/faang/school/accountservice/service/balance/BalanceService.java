package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.enums.auth.payment.AuthPaymentStatus;
import faang.school.accountservice.enums.pending.Category;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static faang.school.accountservice.entity.auth.payment.AuthPaymentBuilder.build;
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
    public AuthPayment authorizePayment(UUID balanceId, Money money) {
        Balance balance = findById(balanceId);
        balanceValidator.checkFreeAmount(balance, money);
//        AuthPayment payment = build(balance, money);
        AuthPayment payment = build(balanceId, balance, balance, money.amount(), Category.OTHER);

        balance.setAuthBalance(balance.getAuthBalance().add(money.amount()));

        saveBalance(balance);
        return saveAuthPayment(payment);
    }

    @Transactional
    public AuthPayment acceptPayment(UUID authPaymentId, Money money) {
        AuthPayment payment = findAuthPaymentBiId(authPaymentId);
        balanceValidator.checkAuthPaymentForAccept(money, payment);

        Balance balance = payment.getBalance();
        BigDecimal newCurrentBalance = balance.getCurrentBalance().subtract(money.amount());
        balance.setCurrentBalance(newCurrentBalance);
        BigDecimal newAuthBalance = balance.getAuthBalance().subtract(payment.getAmount());
        balance.setAuthBalance(newAuthBalance);

        payment.setAmount(money.amount());
        payment.setStatus(AuthPaymentStatus.CLOSED);

        saveBalance(balance);
        return saveAuthPayment(payment);
    }

    @Transactional
    public AuthPayment rejectPayment(UUID authPaymentId) {
        AuthPayment payment = findAuthPaymentBiId(authPaymentId);
        balanceValidator.checkAuthPaymentForReject(payment);

        Balance balance = payment.getBalance();
        BigDecimal newAuthBalance = balance.getAuthBalance().subtract(payment.getAmount());
        balance.setAuthBalance(newAuthBalance);

        payment.setStatus(AuthPaymentStatus.REJECTED);

        saveBalance(balance);
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
    public AuthPayment findAuthPaymentBiId(UUID id) {
        return authPaymentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(AuthPayment.class, id));
    }

    @Transactional(readOnly = true)
    public Balance findById(UUID id) {
        return balanceRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException(Balance.class, id));
    }

    @Transactional
    @Retryable(retryFor = {BalanceHasBeenUpdatedException.class})
    public void saveCashback(Balance balance, BigDecimal cashback) {
        BigDecimal currentBalance = balance.getCurrentBalance();
        balance.setCurrentBalance(currentBalance.add(cashback));
        balance.setUpdatedAt(LocalDateTime.now());

        saveBalance(balance);
    }

    private Balance saveBalance(Balance balance) {
        try {
            balance = balanceRepository.save(balance);
            balanceRepository.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new BalanceHasBeenUpdatedException(balance.getId());
        }
        return balance;
    }

    private AuthPayment saveAuthPayment(AuthPayment payment) {
        try {
            payment = authPaymentRepository.save(payment);
            authPaymentRepository.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new AuthPaymentHasBeenUpdatedException(payment.getId());
        }
        return payment;
    }
}
