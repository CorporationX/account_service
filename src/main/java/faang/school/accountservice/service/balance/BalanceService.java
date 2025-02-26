package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.exception.BalanceConflictException;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AuthPayment;
import faang.school.accountservice.model.AuthPaymentStatus;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Service
@Slf4j
@AllArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceValidator balanceValidator;
    private final AuthPaymentRepository authPaymentRepository;

    @Transactional
    public Balance createBalance(Account account) {
        Balance newBalance = Balance.builder()
                .id(UUID.randomUUID())
                .account(account)
                .build();
        return balanceRepository.save(newBalance);
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

        try {
            balanceRepository.save(balance);
        } catch (OptimisticLockException e) {
            log.error("Conflict during balance update. Try again later", e);
            throw new BalanceConflictException("Conflict during balance update. Please try again.", e);

        }
        return authPaymentRepository.save(authPayment);
    }

    @Transactional
    public AuthPayment acceptPayment(UUID authPaymentId, Money money) {
        AuthPayment payment = findAuthPaymentById(authPaymentId);
        balanceValidator.checkAuthPaymentForAccept(money, payment);

        Balance balance = payment.getBalance();
        BigDecimal newCurrentBalance = balance.getCurrentBalance().subtract(money.amount());
        balance.setCurrentBalance(newCurrentBalance);
        BigDecimal newAuthBalance = balance.getAuthBalance().subtract(payment.getAmount());
        balance.setAuthBalance(newAuthBalance);
        payment.setAmount(money.amount());
        payment.setStatus(AuthPaymentStatus.CLOSED);

        try {
            balanceRepository.save(balance);
        } catch (OptimisticLockException e) {
            log.error("Conflict during balance update in acceptPayment . Try again later", e);
            throw new BalanceConflictException("Conflict during balance update. Please try again.", e);
        }
        return authPaymentRepository.save(payment);
    }

    @Transactional
    public Balance topUpCurrentBalance(UUID balanceId, Money money) {
        Balance balance = findById(balanceId);
        BigDecimal currentBalance = balance.getCurrentBalance();

        balance.setCurrentBalance(currentBalance.add(money.amount()));

        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance multiplyCurrentBalance(UUID balanceId, Double value) {
        Balance balance = findById(balanceId);
        BigDecimal currentBalance = balance.getCurrentBalance();

        BigDecimal multiplier = BigDecimal.valueOf(value);
        BigDecimal newCurrentBalance = currentBalance.add(currentBalance.multiply(multiplier));

        balance.setCurrentBalance(newCurrentBalance);

        return balanceRepository.save(balance);
    }
    @Transactional(readOnly = true)
    public AuthPayment findAuthPaymentById(UUID id) {
        return authPaymentRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Authorization payment with id:", id));
    }
    @Transactional(readOnly = true)
    public Balance findById(UUID id) {

        return balanceRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Balance by id :", id));
    }
}
