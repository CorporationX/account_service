package faang.school.accountservice.service;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.Operation;
import faang.school.accountservice.exception.NotEnoughMoneyAuthorizationException;
import faang.school.accountservice.exception.NotEnoughMoneyException;
import faang.school.accountservice.exception.UnknownOperationException;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {
    private final BalanceRepository balanceRepository;

    @Async
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2.0)
    )
    @Transactional
    public void freezeBalance(Balance balance, double sum) {
        Balance newBalance = balanceRepository.save(balance);
        if (isEnough(sum, newBalance.getBalance())) {
            newBalance.setAuthorizationBalance(sum);
            newBalance.setBalance(newBalance.getBalance() - sum);
        } else {
            log.info("Authorization cannot be completed, there are not enough funds {}", sum);
            throw new NotEnoughMoneyAuthorizationException("You haven't got enough money for authorization");
        }
    }

    @Transactional
    public void createBalance() {
        //TODO: прописать логику после мержа задачи с аккаунтами
    }

    @Async
    @Retryable(
            retryFor = OptimisticLockException.class,
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000, multiplier = 2.0)
    )
    @Transactional
    public void updateBalance(Balance balance, double sum, Operation operation) {
        Balance newBalance = balanceRepository.save(balance);
        switch (operation) {
            case TOP_UP, REFUND -> newBalance.setBalance(newBalance.getBalance() + sum);
            case PAY -> payCase(balance, sum, newBalance);
            default -> {
                log.info("Unknown operation {}", balance); //TODO: заменить на аккаунт
                throw new UnknownOperationException("Unknown operation");
            }
        }
    }

    private void payCase(Balance balance, double sum, Balance newBalance) {
        if (isEnough(sum, newBalance.getBalance())) {
            newBalance.setBalance(newBalance.getBalance() - sum);
        } else {
            log.info("Not enough money {}", balance);
            throw new NotEnoughMoneyException("You haven't got enough money bro");
        }
    }

    private boolean isEnough(double paySum, double balanceSum) {
        return balanceSum >= paySum;
    }
}
