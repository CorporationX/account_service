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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
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
        String accountNumber = balanceDto.getAccountNumber();
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> {
                    log.error("Failed to create balance for account {}: Account not found", accountNumber);
                    return new EntityNotFoundException("Account not found");
                });

        log.info("Account with number {} found. Starting to create balance", accountNumber);
        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setAccountNumber(accountNumber);
        balance.setAuthorizationBalance(isBalanceNotNull(balanceDto)
                ? balanceDto.getAuthorizationBalance()
                : BigDecimal.ZERO);
        balance.setActualBalance(isBalanceNotNull(balanceDto)
                ? balanceDto.getActualBalance()
                : BigDecimal.ZERO);
        balanceRepository.save(balance);
        log.info("Balance with id {} successfully created and saved", balance.getId());

        return mapper.toDto(balance);
    }

    @Transactional
    @Override
    public BalanceDto updateBalance(BalanceDto balanceDto) {
        if (balanceDto.getId() == null) throw new NullPointerException("Balance id cannot be null");
        Balance balance = balanceRepository.findById(balanceDto.getId())
                .orElseThrow(() -> {
                    log.error("Balance not found for id: {}", balanceDto.getId());
                    return new EntityNotFoundException("Balance not found");
                });

        if (isBalanceNotNull(balanceDto)) {
            balance.setAuthorizationBalance(balanceDto.getAuthorizationBalance());
            balance.setActualBalance(balanceDto.getActualBalance());
        }
        balanceRepository.save(balance);
        log.info("Balance with id {} successfully updated", balance.getId());

        return mapper.toDto(balance);
    }

    private boolean isBalanceNotNull(BalanceDto dto) {
        return dto.getActualBalance() != null && dto.getAuthorizationBalance() != null;
    }
}
