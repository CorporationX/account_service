package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.AmountChangeRequest;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.enums.BalanceChangeType;
import faang.school.accountservice.exception.InsufficientFundsException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.BalanceService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final AccountRepository accountRepository;

    private final Map<BalanceChangeType, BalanceChange> balanceChanges = new HashMap<>();

    @Override
    public void registerBalanceChange(BalanceChangeType balanceChangeType, BalanceChange balanceChange) {
        balanceChanges.put(balanceChangeType, balanceChange);
    }

    @Override
    public BalanceDto getBalanceByAccountId(Long accountId) {
        Balance balance = balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Not found balance by account id: " + accountId));
        log.debug("balance by accountId: {}", balance);
        return balanceMapper.toBalanceDto(balance);
    }

    @Override
    public BalanceDto getBalanceById(Long balanceId) {
        Balance balance = getBalance(balanceId);
        log.debug("balance by id: {}", balance);
        return balanceMapper.toBalanceDto(balance);
    }

    @Override
    @Transactional
    public BalanceDto createBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Not found account id: " + accountId));
        Balance currentBalance = account.getCurrentBalance();
        if (currentBalance != null) {
            throw new EntityExistsException("Balance already exists: " + currentBalance.getId());
        }
        Balance balance = Balance.builder()
                .account(account)
                .actualBalance(BigDecimal.ZERO)
                .authBalance(BigDecimal.ZERO)
                .build();
        balanceRepository.save(balance);
        log.info("Created new balance: {}", balance);
        return balanceMapper.toBalanceDto(balance);
    }

    @Override
    @Transactional
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retryable.max-attempts}",
            backoff = @Backoff(delayExpression = "${retryable.delay}"))
    public BalanceDto changeBalance(Long balanceId, AmountChangeRequest amount) {
        Balance balance = getBalance(balanceId);
        balance.setActualBalance(updateBalance(balance.getActualBalance(), amount));
        if (balance.getActualBalance().compareTo(balance.getAuthBalance()) < 0) {
            throw new InsufficientFundsException("Insufficient funds to change balance, auth balance: "
                    + balance.getAuthBalance() + ", balance: " + balance.getActualBalance());
        }
        balanceRepository.save(balance);
        log.debug("balance changed: {}", balance);
        return balanceMapper.toBalanceDto(balance);
    }

    @Override
    @Transactional
    @Retryable(retryFor = {OptimisticLockException.class},
            maxAttemptsExpression = "${retryable.max-attempts}",
            backoff = @Backoff(delayExpression = "${retryable.delay}"))
    public BalanceDto reserveBalance(Long balanceId, AmountChangeRequest amount) {
        Balance balance = getBalance(balanceId);
        BigDecimal authBalance = updateBalance(balance.getAuthBalance(), amount);
        if (authBalance.compareTo(balance.getActualBalance()) > 0) {
            throw new InsufficientFundsException("Insufficient funds to auth balance: " + authBalance);
        }
        balance.setAuthBalance(authBalance);
        balanceRepository.save(balance);
        log.debug("balance authorized: {}", balance);
        return balanceMapper.toBalanceDto(balance);
    }

    private BigDecimal updateBalance(BigDecimal balance, AmountChangeRequest amount) {
        BalanceChange balanceChange = balanceChanges.get(amount.changeType());
        if (balanceChange == null) {
            throw new IllegalArgumentException("Not found balance change type: " + amount.changeType());
        }
        return balanceChange.calculateBalance(balance, amount.amount());
    }

    private Balance getBalance(Long balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Not found balance by id: " + balanceId));
    }
}
