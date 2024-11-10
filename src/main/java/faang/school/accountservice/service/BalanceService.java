package faang.school.accountservice.service;

import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository balanceRepository;

    public Balance updateBalance(Balance balance) {
        return balanceRepository.save(balance);
    }

    @Transactional
    public void increaseAuthBalance(long id, BigDecimal amount) {
        Balance balance = getBalance(id);
        balance.setAuthBalance(balance.getAuthBalance().add(amount));
    }

    @Transactional
    public void decreaseAuthBalance(long id, BigDecimal amount) {
        Balance balance = getBalance(id);
        BigDecimal authBalance = balance.getAuthBalance();
        checkWithdrawMoney(id, authBalance, amount);
        balance.setAuthBalance(authBalance.subtract(amount));
    }

    @Transactional
    public void decreaseActualBalance(long id, BigDecimal amount) {
        Balance balance = getBalance(id);
        BigDecimal actualBalance = balance.getActualBalance();
        checkWithdrawMoney(id, actualBalance, amount);
        balance.setActualBalance(actualBalance.subtract(amount));
    }

    @Transactional
    public void increaseAllBalances(long id, BigDecimal amount) {
        Balance balance = getBalance(id);
        balance.setActualBalance(balance.getActualBalance().add(amount));
        balance.setAuthBalance(balance.getAuthBalance().add(amount));
    }

    private Balance getBalance(long id) {
        return balanceRepository.findById(id).orElseThrow(() -> {
                String message = "Balance with id = %d does not exist".formatted(id);
                return new RuntimeException(message);
            }
        );
    }

    private void checkWithdrawMoney(long balanceId, BigDecimal balanceAmount, BigDecimal amount) {
        if (balanceAmount.compareTo(amount) < 0) {
            String message = "Balance with id = %d does not have enough funds".formatted(balanceId);
            log.error(message);
            throw new RuntimeException(message);
        }
    }

}
