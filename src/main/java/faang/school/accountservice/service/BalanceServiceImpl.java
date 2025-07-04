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

    @Override
    public BalanceDto createBalance(BalanceDto balanceDto) {
        Account account = accountRepository.findByAccountNumber(balanceDto.getAccountNumber())
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        Balance balance = Balance.builder()
                .account(account)
                .build();
//        balance.setActualBalance(balanceDto.getActualBalance());
//        balance.setAuthorizationBalance(balanceDto.getAuthorizationBalance());
        balance = balanceRepository.save(balance);

        return mapper.toDto(balance);
    }

    @Override
    public BalanceDto updateBalance(BalanceDto balanceDto) {
        Balance balance = balanceRepository.findById(balanceDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Balance not found"));
//        balance = balanceRepository.updateBalancesByAuthorizationBalanceAndActualBalance
//                (balanceDto.getAuthorizationBalance(), balanceDto.getActualBalance());
        return mapper.toDto(balance);
    }
}
