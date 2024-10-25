package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceJpaRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper mapper;

    public void create(BalanceDto balanceDto) {
        validateBalanceDto(balanceDto);
        Balance balance = mapper.toEntity(balanceDto);
        balance.setVersion(1);
        balanceRepository.save(balance);
    }

    public void update(BalanceDto balanceDto) {
        validateBalanceDto(balanceDto);
        if (balanceDto.getVersion() < 1) {
            throw new DataValidationException("version cannot be negative");
        }

        Balance balance = mapper.toEntity(balanceDto);
        balance.setUpdatedAt(LocalDateTime.now());
        balance.nextVersion();

        balanceRepository.save(balance);
    }

    public BalanceDto getBalance(long accountId) {
        checkIsAccountExist(accountId);
        Balance balance = balanceRepository.findBalanceByAccount_Id(accountId);
        return mapper.toDto(balance);
    }

    private void checkIsAccountExist(long accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new EntityNotFoundException("Not found account with id = " + accountId);
        }
    }

    private void validateBalanceDto(BalanceDto dto) {
        if (dto.getId() < 1 || dto.getAccountId() < 1) {
            throw new DataValidationException("not Positive id");
        }
    }
}
