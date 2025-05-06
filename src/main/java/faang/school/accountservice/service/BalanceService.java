package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;

    private final AccountRepository accountRepository;

    private final BalanceMapper balanceMapper;

    private final RetryTemplate retryTemplate;

    public BalanceDto updateBalanceSafely(Long accountId, BalanceDto dto) {
        return retryTemplate.execute(context -> update(accountId, dto));
    }

    @Transactional
    public BalanceDto create(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found:  {}", accountId);
                    return new EntityNotFoundException("Account not found: " + accountId);
                });

        if (account.getBalance() != null) {
            log.error("Balance already exists for account ID: {}", accountId);
            throw new IllegalStateException("Balance already exists for account ID: " + accountId);
        }

        Balance balance = Balance.builder()
                .account(account)
                .build();

        balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }

    @Transactional()
    private BalanceDto update(Long accountId, BalanceDto balanceDto) {
        Balance balance = getBalance(accountId);

        balance.setActualBalance(balanceDto.getActualBalance());
        balance.setAuthorizedBalance(balanceDto.getAuthorizedBalance());

        balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }

    @Transactional(readOnly = true)
    public BalanceDto getByAccountId(Long accountId) {
        Balance balance = getBalance(accountId);
        return balanceMapper.toDto(balance);
    }

    private Balance getBalance(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> {
                    log.error("Balance not found for account ID: {}", accountId);
                    return new EntityNotFoundException("Balance not found for account ID: " + accountId);
                });
    }
}
