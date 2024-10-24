package faang.school.accountservice.service.balance;

import com.github.f4b6a3.uuid.UuidCreator;
import faang.school.accountservice.dto.Money;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.balance.AuthPayment;
import faang.school.accountservice.model.balance.AuthPaymentStatus;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.repository.balance.AuthPaymentRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final AuthPaymentRepository authPaymentRepository;
    private final BalanceValidator balanceValidator;

    @Transactional
    public Balance createBalance(Account account) {
        Balance newBalance = Balance.builder()
                .id(UuidCreator.getTimeBased())
                .account(account)
                .build();
        return balanceRepository.save(newBalance);
    }

    @Transactional
    public AuthPayment authorizePayment(UUID balanceId, Money money) {
        Balance balance = findById(balanceId);
        balanceValidator.checkFreeAmount(balance, money);
        AuthPayment payment = AuthPayment.builder()
                .id(UUID.randomUUID())
                .balance(balance)
                .amount(money.amount())
                .build();
        balance.setAuthBalance(balance.getAuthBalance().add(money.amount()));
        balanceRepository.save(balance);
        return authPaymentRepository.save(payment);
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

        balanceRepository.save(balance);
        return authPaymentRepository.save(payment);
    }

    @Transactional
    public AuthPayment rejectPayment(UUID authPaymentId) {
        AuthPayment payment = findAuthPaymentBiId(authPaymentId);
        balanceValidator.checkAuthPaymentForReject(payment);

        Balance balance = payment.getBalance();
        BigDecimal newAuthBalance = balance.getAuthBalance().subtract(payment.getAmount());
        balance.setAuthBalance(newAuthBalance);

        payment.setStatus(AuthPaymentStatus.REJECTED);

        balanceRepository.save(balance);
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
    public AuthPayment findAuthPaymentBiId(UUID id) {
        return authPaymentRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Authorization payment with id:", id));
    }

    @Transactional(readOnly = true)
    public Balance findById(UUID id) {
        return balanceRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Balance by id:", id));
    }
}
