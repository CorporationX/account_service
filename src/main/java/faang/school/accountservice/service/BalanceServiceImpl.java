package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService{

    private final AccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    @Transactional
    @Override
    public BalanceDto createBalance(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        Balance balance = createNewBalance(account);
        createBalanceAudit(balance, null);
        return balanceMapper.toDto(balance);
    }

    @Override
    public BalanceDto getBalance(Long accountId){
        Balance balance = getBalanceByAccountId(accountId);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public BalanceDto updateBalance(Long accountId, BigDecimal newCurrentBalance, BigDecimal newAuthorizedBalance){
        Balance balance = getBalanceByAccountId(accountId);
        updateBalanceValues(balance, newCurrentBalance, newAuthorizedBalance);
        return balanceMapper.toDto(balance);
    }

    private Balance createNewBalance(Account account) {
        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setCurrentBalance(BigDecimal.ZERO);
        balance.setAuthorizedBalance(BigDecimal.ZERO);
        balance.setCreatedAt(LocalDateTime.now());
        return balanceRepository.save(balance);
    }

    private Balance getBalanceByAccountId(Long accountId) {
        return balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Balance not found for account"));
    }

    private void updateBalanceValues(Balance balance, BigDecimal newCurrentBalance, BigDecimal newAuthorizedBalance) {
        balance.setCurrentBalance(newCurrentBalance);
        balance.setAuthorizedBalance(newAuthorizedBalance);
        balance.setUpdatedAt(LocalDateTime.now());
        balanceRepository.save(balance);
    }

    private void createBalanceAudit(Balance balance, Long operationId){
        BalanceAudit balanceAudit = balanceAuditMapper.fromBalance(balance, operationId);
        balanceAuditRepository.save(balanceAudit);

    }
}
