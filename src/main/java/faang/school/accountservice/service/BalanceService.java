package faang.school.accountservice.service;

import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceReadDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.BusinessException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Validated
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public BalanceReadDto getBalanceDto(long balanceId) {
        return balanceMapper.toDto(getBalance(balanceId));
    }

    @Transactional
    public BalanceReadDto createBalance(BalanceCreateDto createDto) {
        Balance newBalance = balanceMapper.toEntity(createDto);
        newBalance = balanceRepository.save(newBalance);
        return balanceMapper.toDto(newBalance);
    }

    @Transactional
    public BalanceReadDto increaseBalance(long balanceId, @Positive BigDecimal amount) {
        Balance balance = getBalance(balanceId);
        balance.setActualBalance(balance.getAuthorizedBalance().add(amount));
        balance = balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceReadDto decreaseBalance(long balanceId, @Positive BigDecimal amount) {
        Balance balance = getBalance(balanceId);
        if (balance.getActualBalance().compareTo(amount) < 0) {
            throw new BusinessException("Невозможно списать средства превышающие текущий баланс");
        }
        balance.setActualBalance(balance.getAuthorizedBalance().subtract(amount));
        balance = balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceReadDto reserveBalance(long balanceId, @Positive BigDecimal amount) {
        Balance balance = getBalance(balanceId);
        if (balance.getActualBalance().compareTo(amount) < 0) {
            throw new BusinessException("Невозможно зарезервировать средства превышающие текущий баланс");
        }
        balance.setActualBalance(balance.getAuthorizedBalance().subtract(amount));
        balance.setAuthorizedBalance(balance.getAuthorizedBalance().add(amount));
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceReadDto releaseReservedBalance(long balanceId, @Positive BigDecimal amount) {
        Balance balance = getBalance(balanceId);
        if (balance.getAuthorizedBalance().compareTo(amount) < 0) {
            throw new BusinessException("Невозможно освободить больше средств, чем зарезервировано");
        }
        balance.setAuthorizedBalance(balance.getAuthorizedBalance().subtract(amount));
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceReadDto cancelBalanceReservation(long balanceId) {
        Balance balance = getBalance(balanceId);
        balance.setActualBalance(balance.getActualBalance().add(balance.getAuthorizedBalance()));
        balance.setAuthorizedBalance(BigDecimal.ZERO);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public Balance getBalance(long balanceId) {
        return balanceRepository.getReferenceById(balanceId);
    }
}
