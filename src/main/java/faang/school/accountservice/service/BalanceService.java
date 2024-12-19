package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.BalanceBelowZeroException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
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
    private final BalanceMapper balanceMapper;

    @Transactional
    public void createBalance(Account account) {
        Balance balance = new Balance();
        balance.setActualBalance(BigDecimal.ZERO);
        balance.setAuthorizationBalance(BigDecimal.ZERO);
        balance.setAccount(account);
        balance.setCreatedAt(LocalDateTime.now());
        balance.setUpdatedAt(LocalDateTime.now());
        log.info("Balance for {} account is created", account.getAccountNumber());
        balanceRepository.save(balance);
    }

    public BalanceDto getBalance(Long accountId) {
        log.info("Balance for {} account is find", accountId);
        return balanceMapper.toDto(balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Balance not found")));
    }

    @Transactional
    public BalanceDto updateBalance(Long accountId, TransactionDto transaction) {
        Balance balance = balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Balance not found"));

        BigDecimal amount = transaction.getAmount();
        BigDecimal actualBalance = balance.getActualBalance().add(amount);
        BigDecimal authorizationBalance = balance.getAuthorizationBalance().add(amount);
        int version = balance.getVersion() + 1;

        if (authorizationBalance.compareTo(BigDecimal.ZERO) < 0 ||
                actualBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new BalanceBelowZeroException(accountId, amount);
        }

        balance.setActualBalance(actualBalance);
        balance.setAuthorizationBalance(authorizationBalance);
        balance.setVersion(version);
        balance.setUpdatedAt(LocalDateTime.now());
        log.info("Balance for {} account is updated", accountId);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }
}