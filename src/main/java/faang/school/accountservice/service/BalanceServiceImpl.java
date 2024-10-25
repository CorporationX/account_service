package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Service
@Validated
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceJpaRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper mapper;

    public void create(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balanceRepository.save(balance);
    }

    public void update(BalanceDto balanceDto) {
        Balance balance = mapper.toEntity(balanceDto);
        balance.setUpdatedAt(LocalDateTime.now());
        balanceRepository.save(balance);
    }

    public BalanceDto getBalance(long accountId) {
        checkIsAccountExist(accountId);
        Balance balance = balanceRepository.findBalanceByAccountId(accountId);
        return mapper.toDto(balance);
    }

    private void checkIsAccountExist(long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new EntityNotFoundException("Not found account with id = " + accountId);
        }
    }
}
