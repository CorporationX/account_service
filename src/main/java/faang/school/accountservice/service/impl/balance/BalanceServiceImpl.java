package faang.school.accountservice.service.impl.balance;

import faang.school.accountservice.exception.InsufficientBalanceException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.dto.balance.BalanceDto;
import faang.school.accountservice.model.dto.balance.TransferResultDto;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.service.BalanceService;
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
    private final BalanceMapper balanceMapper;

    @Override
    @Transactional(readOnly = true)
    public BalanceDto getBalance(long balanceId) {
        Balance balance = getBalanceOrThrow(balanceId);
        return balanceMapper.toDto(balance);
    }

    @Override
    @Transactional
    public BalanceDto createBalance(BalanceDto balanceDto) {
        Balance balance = balanceMapper.toEntity(balanceDto);
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Override
    @Transactional
    public BalanceDto increaseBalance(long balanceId, BigDecimal amount) {
        Balance balance = getBalanceOrThrow(balanceId);
        balance.setCurrentActualBalance(balance.getCurrentActualBalance().add(amount));
        balance.setUpdatedAt(LocalDateTime.now());
        Balance saved = balanceRepository.save(balance);
        return balanceMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BalanceDto decreaseBalance(long balanceId, BigDecimal amount) {
        Balance balance = getBalanceOrThrow(balanceId);
        if (balance.getCurrentActualBalance().compareTo(amount) < 0)
            throw new InsufficientBalanceException("Insufficient funds on balance");
        balance.setCurrentActualBalance(balance.getCurrentActualBalance().subtract(amount));
        balance.setUpdatedAt(LocalDateTime.now());
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Override
    @Transactional
    public BalanceDto reserveBalance(long balanceId, BigDecimal amount) {
        Balance balance = getBalanceOrThrow(balanceId);
        if (balance.getCurrentActualBalance().compareTo(amount) < 0)
            throw new InsufficientBalanceException("Insufficient funds on balance for reservation");
        balance.setCurrentActualBalance(balance.getCurrentActualBalance().subtract(amount));
        balance.setCurrentAuthorizationBalance(balance.getCurrentAuthorizationBalance().add(amount));
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Override
    @Transactional
    public BalanceDto releaseReservedBalance(long balanceId, BigDecimal amount) {
        Balance balance = getBalanceOrThrow(balanceId);
        if (balance.getCurrentAuthorizationBalance().compareTo(amount) < 0)
            throw new InsufficientBalanceException("Cannot release more than the reserved amount");
        balance.setCurrentAuthorizationBalance(balance.getCurrentAuthorizationBalance().subtract(amount));
        //Нужна логика для отправки на другой счёт
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Override
    @Transactional
    public BalanceDto cancelReservation(long balanceId) {
        Balance balance = getBalanceOrThrow(balanceId);
        balance.setCurrentActualBalance(balance.getCurrentActualBalance().add(balance.getCurrentAuthorizationBalance()));
        balance.setCurrentAuthorizationBalance(balance.getCurrentAuthorizationBalance().subtract(balance.getCurrentAuthorizationBalance()));
        return balanceMapper.toDto(balanceRepository.save(balance));
    }

    @Override
    @Transactional
    public TransferResultDto transferBalance(long fromAccountId, long toAccountId, BigDecimal amount) {
        if (fromAccountId == toAccountId)
            throw new IllegalArgumentException("Cannot transfer to same account");
        Balance fromBalance = getBalanceOrThrow(fromAccountId);
        Balance toBalance = getBalanceOrThrow(toAccountId);
        if (fromBalance.getCurrentActualBalance().compareTo(amount) < 0)
            throw new InsufficientBalanceException("Insufficient funds on balance for transfer");
        fromBalance.setCurrentActualBalance(fromBalance.getCurrentActualBalance().subtract(amount));
        toBalance.setCurrentActualBalance(toBalance.getCurrentActualBalance().add(amount));
        Balance from = balanceRepository.save(fromBalance);
        Balance to = balanceRepository.save(toBalance);
        return balanceMapper.toTransferResultDto(from, to);
    }

    private Balance getBalanceOrThrow(long balanceId) {
        return balanceRepository.findById(balanceId)
                .orElseThrow(() -> new EntityNotFoundException("Balance with ID %d doesn't exist!".formatted(balanceId)));
    }
}
