package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.repository.account.AccountRepository;
import faang.school.accountservice.repository.balance.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    @Transactional(readOnly = true)
    @Override
    public BalanceDto getBalanceByAccountId(long accountId) {
        if (accountRepository.findById(accountId).isEmpty()) {
            throw new EntityNotFoundException(String.format("Account with id = %d not found", accountId));
        }
        return balanceRepository.findByAccountId(accountId);
    }

    @Transactional
    @Override
    public BalanceDto createBalance(BalanceDto dto) {
        Account account = accountRepository.findById(dto.accountId()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Account with id = %d not found", dto.accountId())));
        Balance balance = balanceMapper.toEntity(dto);
        balance.setAccount(account);
        balance.setAuthorizationBalance(BigDecimal.ZERO);
        balance.setActualBalance(BigDecimal.ZERO);
        balance.setCreatedAt(LocalDateTime.now());
        balance.setUpdatedAt(LocalDateTime.now());
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Transactional
    @Override
    public BalanceDto updateBalance(BalanceDto dto) {
        Balance balance = balanceRepository.findById(dto.id()).orElseThrow(() ->
                new EntityNotFoundException(String.format("Balance with id = %d not found", dto.id())));
        Balance updatedBalance = balanceMapper.update(dto, balance);
        updatedBalance.setUpdatedAt(LocalDateTime.now());
        return balanceMapper.toDto(updatedBalance);
    }
}