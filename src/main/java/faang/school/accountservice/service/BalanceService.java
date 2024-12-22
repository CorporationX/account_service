package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.exception.BalanceBelowZeroException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public BalanceDto createBalance(Account account) {
        Balance balance = new Balance();
        balance.setAccount(account);
        balance = balanceRepository.save(balance);
        log.info("Balance for {} account is created", account.getAccountNumber());

        createBalanceAudit(balance, 1L);

        return balanceMapper.toDto(balance);
    }

    public BalanceDto getBalance(Long accountId) {
        log.info("Balance for {} account is find", accountId);
        return balanceMapper.toDto(getBalanceFromRepository(accountId));
    }

    @Transactional
    public BalanceDto updateBalance(Long accountId, TransactionDto transaction) {
        Balance balance = getBalanceFromRepository(accountId);

        BigDecimal amount = transaction.getAmount();
        BigDecimal actualBalance = balance.getActualBalance();
        BigDecimal authorizationBalance = balance.getAuthorizationBalance();

        switch (transaction.getOperationType()) {
            case CLEARING -> actualBalance = actualBalance.add(amount);
            case AUTHORIZATION -> authorizationBalance = authorizationBalance.add(amount);
        }

        if (authorizationBalance.compareTo(BigDecimal.ZERO) < 0 ||
                actualBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BalanceBelowZeroException(accountId, amount);
        }
        balance.setActualBalance(actualBalance);
        balance.setAuthorizationBalance(authorizationBalance);
        balance.setVersion(balance.getVersion() + 1);
        balance.setUpdatedAt(LocalDateTime.now());
        balance = balanceRepository.save(balance);

        createBalanceAudit(balance, transaction.getOperationId());

        log.info("Balance for {} account is updated", accountId);
        return balanceMapper.toDto(balance);
    }

    private Balance getBalanceFromRepository(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found"));
    }

    private void createBalanceAudit(Balance balance, Long operationId) {
        BalanceAudit audit = BalanceAudit.builder()
                .account(balance.getAccount())
                .balanceVersion(balance.getVersion())
                .authorizationBalance(balance.getAuthorizationBalance())
                .actualBalance(balance.getActualBalance())
                .operationId(operationId)
                .createdAt(LocalDateTime.now())
                .build();
        balanceAuditRepository.save(audit);
        log.info("Balance audit for account {} with version {} is created",
                balance.getAccount().getId(), balance.getVersion());
    }
}
