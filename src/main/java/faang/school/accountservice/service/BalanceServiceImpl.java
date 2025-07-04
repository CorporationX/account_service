package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper mapper;
    private final AccountRepository accountRepository;

    @Override
    public BalanceDto getBalanceById(Long id) {
        Balance balance = balanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Balance not found"));
        return mapper.toDto(balance);
    }

    @Transactional
    @Override
    public BalanceDto createBalance(BalanceDto balanceDto) {
        Account account = accountRepository.findByAccountNumber(balanceDto.getAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setAuthorizationBalance(balanceDto.getAuthorizationBalance() != null
                ? balanceDto.getAuthorizationBalance()
                : BigDecimal.ZERO);
        balance.setActualBalance(balanceDto.getActualBalance() != null
                ? balanceDto.getActualBalance()
                : BigDecimal.ZERO);

        balanceRepository.save(balance);

        return mapper.toDto(balance);
    }

    @Transactional
    @Override
    public BalanceDto updateBalance(BalanceDto balanceDto) {
        Balance balance = balanceRepository.findById(balanceDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Balance not found for ID: " + balanceDto.getId()));

        if (balanceDto.getAuthorizationBalance() != null) {
            balance.setAuthorizationBalance(balanceDto.getAuthorizationBalance());
        }
        if (balanceDto.getActualBalance() != null) {
            balance.setActualBalance(balanceDto.getActualBalance());
        }

        balanceRepository.save(balance);

        return mapper.toDto(balance);
    }
}

