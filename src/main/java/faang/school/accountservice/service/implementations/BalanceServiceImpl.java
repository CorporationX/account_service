package faang.school.accountservice.service.implementations;

import faang.school.accountservice.dto.BalanceOperationDto;
import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.interfaces.BalanceService;
import faang.school.accountservice.strategy.BalanceOperationStrategy;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final List<BalanceOperationStrategy> balanceOperationStrategies;

    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public BalanceResponseDto createBalance(Long accountId) {
        log.info("Create account balance for account ID: {}", accountId);
        Account account = findAccountByAccountId(accountId);
        if (account.getBalance() != null) {
            log.error("Balance already exists for account ID: {}", accountId);
            throw new EntityExistsException("Balance already exists for account: " + accountId);
        }
        Balance balance = Balance.builder()
                .account(account)
                .actualBalance(BigDecimal.ZERO)
                .authorizedBalance(BigDecimal.ZERO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        account.setBalance(balance);
        balanceRepository.save(balance);

        log.info("Created new balance with id: {}", balance.getId());

        return balanceMapper.toBalanceResponseDto(balance);
    }

    @Override
    @Transactional
    @Retryable(
            retryFor = OptimisticLockingFailureException.class,
            backoff = @Backoff(delay = 1000)
    )
    public BalanceResponseDto updateBalance(Long accountId, BalanceOperationDto dto) {
        log.info("Update account balance for account ID: {}", accountId);
        Balance balance = findBalanceByAccountId(accountId);

        BalanceOperationStrategy strategy = balanceOperationStrategies.stream()
                .filter(o -> o.isApplicable(dto.getOperationType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported operation type: " + dto.getOperationType()));
        strategy.apply(balance, dto.getAmount());

        balance.setUpdatedAt(LocalDateTime.now());

        balanceRepository.save(balance);
        log.info("Updated account balance with id: {}", balance.getId());

        return balanceMapper.toBalanceResponseDto(balance);
    }

    @Override
    @Transactional(readOnly = true)
    public BalanceResponseDto getBalance(Long accountId) {
        Balance balance = findBalanceByAccountId(accountId);
        log.info("Successfully got balance for accountId {} with id: {}", accountId, balance.getId());
        return balanceMapper.toBalanceResponseDto(balance);
    }

    private Balance findBalanceByAccountId(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found for account ID: " + accountId));
    }

    private Account findAccountByAccountId(Long accountId) {
        return accountRepository.findByIdWithBalance(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found for account ID: " + accountId));
    }
}
