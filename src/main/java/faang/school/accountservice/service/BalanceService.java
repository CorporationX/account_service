package faang.school.accountservice.service;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BalanceService {
    private final BalanceRepository balanceRepository;

    @Autowired
    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    public Balance createBalanceForAccount(Long accountId, BigDecimal initialBalance) {
        Balance balance = new Balance();
        // account будет установлен через связь
        balance.setActualBalance(initialBalance);
        balance.setAuthorizedBalance(BigDecimal.ZERO);
        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance authorizeAmount(Long accountId, BigDecimal amount) {
        Balance balance = balanceRepository.findByAccountIdWithOptimisticLock(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found for account: " + accountId));

        if (balance.getActualBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));
        balance.setActualBalance(balance.getActualBalance().subtract(amount));

        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance captureAmount(Long accountId, BigDecimal amount) {
        Balance balance = balanceRepository.findByAccountIdWithOptimisticLock(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found for account: " + accountId));

        if (balance.getAuthorizedBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Not enough authorized funds");
        }

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance reverseAuthorization(Long accountId, BigDecimal amount) {
        Balance balance = balanceRepository.findByAccountIdWithOptimisticLock(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found for account: " + accountId));

        if (balance.getAuthorizedBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Cannot reverse more than authorized");
        }

        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
        balance.setActualBalance(balance.getActualBalance().add(amount));

        return balanceRepository.save(balance);
    }

    @Transactional
    public Balance addFunds(Long accountId, BigDecimal amount) {
        Balance balance = balanceRepository.findByAccountIdWithOptimisticLock(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found for account: " + accountId));

        balance.setActualBalance(balance.getActualBalance().add(amount));
        return balanceRepository.save(balance);
    }

    public Balance getBalanceByAccountId(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException("Balance not found for account: " + accountId));
    }
}
