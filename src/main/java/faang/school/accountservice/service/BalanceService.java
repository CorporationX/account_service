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

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    public BalanceDto getBalance(Long balanceId) {
        Balance balance = balanceRepository
                .findById(balanceId).orElseThrow(() -> new EntityNotFoundException("Balance not found"));
        return balanceMapper.toDto(balance);
    }

    public BalanceDto createBalance(BalanceDto balanceDto) {
        Account account = accountRepository
                .findById(balanceDto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        Balance balance = Balance.builder()
                .account(account)
                .authorization_Balance(BigDecimal.ZERO)
                .actual_Balance(BigDecimal.ZERO)
                .build();

        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    public BalanceDto updateBalance(BalanceDto balanceDto) {
        return balanceMapper.toDto(balanceRepository.save(balanceMapper.toEntity(balanceDto)));
    }
}
