package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.exception.ResourceNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.AccountRepository;
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

    @Transactional
    @Override
    public BalanceDto createBalance(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setCurrentBalance(BigDecimal.ZERO);
        balance.setAuthorizedBalance(BigDecimal.ZERO);
        balance.setCreatedAt(LocalDateTime.now());
        balance = balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }

    @Override
    public BalanceDto getBalance(Long accountId){
        Balance balance = balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Balance not found for account"));
        return balanceMapper.toDto(balance);
    }

    @Transactional
    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public BalanceDto updateBalance(Long accountId, BigDecimal newCurrentBalance, BigDecimal newAuthorizedBalance){
        Balance balance = balanceRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Balance not found for account"));
        balance.setCurrentBalance(newCurrentBalance);
        balance.setAuthorizedBalance(newAuthorizedBalance);
        balance.setUpdatedAt(LocalDateTime.now());
        balance = balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }
}
