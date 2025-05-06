package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
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
    private final BalanceAuditRepository balanceAuditRepository;
    private final AccountRepository accountRepository;

    private final BalanceMapper balanceMapper;
    private final BalanceAuditMapper balanceAuditMapper;

    private final RetryTemplate retryTemplate;

    public BalanceDto updateBalanceSafely(Long accountId, BalanceDto dto) {
        return retryTemplate.execute(context -> update(accountId, dto));
    }

    @Transactional
    public BalanceDto create(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found: " + accountId));

        if (account.getBalance() != null) {
            throw new IllegalStateException("Balance already exists for account ID: " + accountId);
        }

        Balance balance = Balance.builder()
                .account(account)
                .build();

        balance =  balanceRepository.save(balance);
        BalanceAudit audit =  balanceAuditMapper.toAudit(balance,null);

        balanceAuditRepository.save(audit);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    private BalanceDto update(Long accountId, BalanceDto balanceDto) {
        Balance balance = getBalance(accountId);

        balance.setActualBalance(balanceDto.getActualBalance());
        balance.setAuthorizedBalance(balanceDto.getAuthorizedBalance());

        balance =  balanceRepository.save(balance);

        BalanceAudit audit = balanceAuditMapper.toAudit(balance,null);// тут null я смогу изменит ток после того как сделают операций
        balanceAuditRepository.save(audit);

        return balanceMapper.toDto(balance);
    }

    @Transactional(readOnly = true)
    public BalanceDto getByAccountId(Long accountId) {
        Balance balance = getBalance(accountId);
        return balanceMapper.toDto(balance);
    }

    private Balance getBalance(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found for account ID: " + accountId));
    }
}
