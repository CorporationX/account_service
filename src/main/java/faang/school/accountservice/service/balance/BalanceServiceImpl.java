package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.AmountChangeRequest;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.service.balance.changebalance.BalanceChange;
import faang.school.accountservice.service.balance.changebalance.BalanceChangeStorage;
import faang.school.accountservice.service.balance.operation.Operation;
import faang.school.accountservice.service.balance.operation.OperationStorage;
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

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final AccountRepository accountRepository;
    private final OperationStorage operationStorage;
    private final BalanceChangeStorage balanceChangeStorage;

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
        Operation operation = operationStorage.getOperation(amount.operationType());
        BalanceChange balanceChange = balanceChangeStorage.getBalanceChange(amount.changeBalanceType());
        balance = balanceChange.processBalance(balance, amount.amount(), operation);
        balanceRepository.save(balance);
        log.debug("balance changed: {}", balance);
        return balanceMapper.toBalanceDto(balance);
    }

    private Balance getBalance(Long balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Not found balance by id: " + balanceId));
    }
}
