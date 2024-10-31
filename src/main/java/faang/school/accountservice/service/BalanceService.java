package faang.school.accountservice.service;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAuthPayment;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuthPaymentRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.OptimisticLockException;
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
                .authorization(BigDecimal.valueOf(0))
                .actual(BigDecimal.valueOf(0.0))
                .build();

        return balanceRepository.save(balance);
    }

    @Transactional
    public BalanceAuthPayment authPayment(UUID balanceId, Money money) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow();

        BigDecimal currentBalanceAmount = balance.getActual().subtract(balance.getAuthorization());

        if (money.amount().compareTo(currentBalanceAmount) < 0) {
            throw new ValidationException("Not enough money");
        }

        BalanceAuthPayment authPayment = BalanceAuthPayment.builder()
                .balance(balance)
                .amount(money.amount())
                .build();

        balance.setAuthorization(balance.getAuthorization().add(money.amount()));
        balanceRepository.save(balance);

        try {
            authPayment = balanceAuthPaymentRepository.save(authPayment);
            balanceAuthPaymentRepository.flush();
        } catch (OptimisticLockingFailureException exception) {
            throw new RuntimeException(String.format("Error authorization payment %s", authPayment.getId()));
        }
        return authPayment;
    }

    @Transactional
    public Balance topUpCurrentBalance(UUID balanceId, Money money) {
        Balance balance = balanceRepository.findById(balanceId).orElseThrow();

        BigDecimal currentBalance = balance.getActual();
        balance.setAuthorization(currentBalance.add(money.amount()));
        return balanceRepository.save(balance);
    }


    @Transactional
    public Balance updateBalance(UUID accountUuid, Balance balance) {
        Account account = accountRepository.findById(accountUuid)
                .orElseThrow();

        Balance storedBalance = account.getBalance();
        storedBalance.setAuthorization(balance.getAuthorization());
        storedBalance.setActual(balance.getActual());

        try {
            return balanceRepository.save(storedBalance);
        } catch (OptimisticLockException e) {
            log.error(e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Balance getBalance(UUID accountUuid) {
        Account account = accountRepository.findById(accountUuid)
                .orElseThrow();

        return account.getBalance();
    }
}
