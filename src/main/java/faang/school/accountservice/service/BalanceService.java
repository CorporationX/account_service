package faang.school.accountservice.service;

import faang.school.accountservice.annotations.NoTransactional;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.non_retryable.EntityNotFoundException;
import faang.school.accountservice.exception.non_retryable.NotEnoughFundsException;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;

    @Transactional(readOnly = true)
    public Balance findBalanceByAccountIdOrThrow(Long id) throws EntityNotFoundException {
        return balanceRepository.findByAccountIdOrThrow(id);
    }

    @Transactional
    public void freezeMoneyAtBalance(Balance balance, BigDecimal moneyAmount) throws NotEnoughFundsException {
        BigDecimal currentBalance = balance.getCurrentBalance();
        checkEnoughFunds(balance, moneyAmount, currentBalance);

        BigDecimal authBalance = balance.getAuthBalance();

        balance.setCurrentBalance(
                currentBalance.subtract(moneyAmount));
        balance.setAuthBalance(
                authBalance.add(moneyAmount));
        balanceRepository.save(balance);
    }

    @Transactional
    public void addFundsToCurrentBalance(Balance balance, BigDecimal moneyAmount) {
        balance.setCurrentBalance(balance.getCurrentBalance().add(moneyAmount));
        balanceRepository.save(balance);
    }

    public void payFromCurrentBalance(Balance balance, BigDecimal moneyAmount) {
        BigDecimal currentBalance = balance.getCurrentBalance();

        checkEnoughFunds(balance, moneyAmount, currentBalance);

        balance.setCurrentBalance(currentBalance.subtract(moneyAmount));
        balanceRepository.save(balance);
    }

    @NoTransactional
    public void writeOffFromAuthBalance(Balance balance, BigDecimal moneyAmount) throws NotEnoughFundsException {
        BigDecimal authBalance = balance.getAuthBalance();

        checkEnoughFunds(balance, moneyAmount, authBalance);

        balance.setAuthBalance(authBalance.subtract(moneyAmount));
        balanceRepository.save(balance);
    }

    @NoTransactional
    public void increaseFromAnotherAuthBalance(Balance balance, BigDecimal moneyAmount) {
        balance.setCurrentBalance(balance.getCurrentBalance().add(moneyAmount));
        balanceRepository.save(balance);
    }

    @NoTransactional
    public void unfreezeMoney(Balance balance, BigDecimal moneyAmount) throws NotEnoughFundsException {
        BigDecimal authBalance = balance.getAuthBalance();

        checkEnoughFunds(balance, moneyAmount, authBalance);

        balance.setAuthBalance(authBalance.subtract(moneyAmount));
        balance.setCurrentBalance(balance.getCurrentBalance().add(moneyAmount));

        balanceRepository.save(balance);
    }

    private static void checkEnoughFunds(Balance balance, BigDecimal moneyAmount, BigDecimal authBalance) {
        if (authBalance.compareTo(moneyAmount) < 0) {
            throw new NotEnoughFundsException("Not enough funds at balance id: " + balance.getId());
        }
    }
}
