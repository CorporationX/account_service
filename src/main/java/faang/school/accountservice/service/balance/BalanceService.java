package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AuthPayment;
import faang.school.accountservice.entity.AuthPaymentStatus;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.BalanceConflictException;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import faang.school.accountservice.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Slf4j
@AllArgsConstructor
@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceValidator balanceValidator;
    private final AuthPaymentRepository authPaymentRepository;
    private final AccountService accountService;


    @Transactional
    public Balance createBalanceForAccount(Long id) {
        Account account = accountService.getAccountById(id);

        Balance newBalance = Balance.builder()
                .id(UUID.randomUUID())
                .account(account)
                .build();
        return saveBalanceWithOptimisticLockHandling(newBalance);
    }

    @Transactional
    public AuthPayment authorizePayment(UUID balanceId, Money money) {
        Balance balance = findById(balanceId);
        balanceValidator.checkFreeAmount(balance, money);

        AuthPayment authPayment = AuthPayment.builder()
                .id(UUID.randomUUID())
                .balance(balance)
                .amount(money.amount())
                .build();
        balance.setAuthBalance(balance.getAuthBalance().add(money.amount()));

        saveBalanceWithOptimisticLockHandling(balance);

        return saveAuthPaymentWithOptimisticLockHandling(authPayment);
    }

    @Transactional
    public AuthPayment acceptPayment(UUID authPaymentId, Money money) {
        AuthPayment authPayment = findAuthPaymentById(authPaymentId);
        balanceValidator.checkAuthPaymentForAccept(money, authPayment);

        Balance balance = authPayment.getBalance();
        BigDecimal newCurrentBalance = balance.getCurrentBalance().subtract(money.amount());
        balance.setCurrentBalance(newCurrentBalance);

        BigDecimal newAuthBalance = balance.getAuthBalance().subtract(authPayment.getAmount());
        balance.setAuthBalance(newAuthBalance);
        authPayment.setAmount(money.amount());

        authPayment.setStatus(AuthPaymentStatus.CLOSED);

        saveBalanceWithOptimisticLockHandling(balance);

        return saveAuthPaymentWithOptimisticLockHandling(authPayment);
    }

    @Transactional
    public AuthPayment rejectPayment(UUID authPaymentId) {
        AuthPayment authPayment = findAuthPaymentById(authPaymentId);
        balanceValidator.checkAuthPaymentForReject(authPayment);

        Balance balance = authPayment.getBalance();
        BigDecimal newAuthBalance = balance.getAuthBalance().subtract(authPayment.getAmount());
        balance.setAuthBalance(newAuthBalance);

        authPayment.setStatus(AuthPaymentStatus.REJECTED);

        saveBalanceWithOptimisticLockHandling(balance);

        return authPaymentRepository.save(authPayment);
    }

    @Transactional
    public Balance topUpCurrentBalance(UUID balanceId, Money money) {
        Balance balance = findById(balanceId);
        BigDecimal currentBalance = balance.getCurrentBalance();

        balance.setCurrentBalance(currentBalance.add(money.amount()));

        return saveBalanceWithOptimisticLockHandling(balance);
    }

    @Transactional
    public Balance multiplyCurrentBalance(UUID balanceId, Double value) {
        Balance balance = findById(balanceId);
        BigDecimal currentBalance = balance.getCurrentBalance();

        BigDecimal multiplier = BigDecimal.valueOf(value);
        BigDecimal newCurrentBalance = currentBalance.add(currentBalance.multiply(multiplier));

        balance.setCurrentBalance(newCurrentBalance);

        return saveBalanceWithOptimisticLockHandling(balance);
    }

    @Transactional(readOnly = true)
    public AuthPayment findAuthPaymentById(UUID authPaymentId) {

        return authPaymentRepository.findById(authPaymentId).orElseThrow(() ->
                new ResourceNotFoundException("Authorization payment with id:", authPaymentId));
    }

    @Transactional(readOnly = true)
    public Balance findById(UUID id) {

        return balanceRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Balance by id :", id));
    }

    @Transactional(readOnly = true)
    public BigDecimal checkBalanceFindById(UUID id) {
        Balance byId = findById(id);
        BigDecimal currentBalance = byId.getCurrentBalance();

        return currentBalance;
    }

    private Balance saveBalanceWithOptimisticLockHandling(Balance balance) {
        try {

            return balanceRepository.save(balance);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Conflict, balance was updated by another process. Try again later", e);
            throw new BalanceConflictException("Conflict during balance update. Please try again.", e);
        }
    }

    private AuthPayment saveAuthPaymentWithOptimisticLockHandling(AuthPayment authPayment) {
        try {

            return authPaymentRepository.save(authPayment);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.error("Conflict, authPayment was updated by another process. Try again later", e);
            throw new BalanceConflictException("Conflict during balance update. Please try again.", e);
        }
    }
}