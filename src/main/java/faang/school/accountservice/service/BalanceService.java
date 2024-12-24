package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.mappers.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.exception.BalanceConflictException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    public BalanceDto getBalanceByAccountId(Long accountId) {
        log.info("Getting balance by account id {}", accountId);
        Balance balance = balanceRepository.findById(accountId)
                .orElseThrow(() -> new BalanceNotFoundException("Balance not found for account ID: " + accountId));
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto createBalance(BalanceDto balanceDto) {
        log.info("Creating balance {}", balanceDto);
        Balance balance = balanceMapper.toEntity(balanceDto);
        balance = balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public BalanceDto updateBalance(Long accountId, BalanceDto balanceDto) {
        log.info("Updating balance {}", balanceDto);
        Balance balance = balanceRepository.findById(accountId)
                .orElseThrow(() -> new BalanceNotFoundException("Balance not found for account ID: " + accountId));

        balance.setAuthBalance(balanceDto.getAuthBalance());
        balance.setActualBalance(balanceDto.getActualBalance());

        try {
            balance = balanceRepository.save(balance);
        } catch (OptimisticLockException e) {
            log.warn("Conflict during balance update. Try again later.");
            throw new BalanceConflictException("Conflict during balance update. Try again later.", e);
        }

        return balanceMapper.toDto(balance);
    }

    /**
     * Метод добавляет зарезервированную сумму
     * **/
    @Transactional
    public void authorizePayment(Account account, BigDecimal amount) {
        Balance balance = account.getBalance();
        balance.setAuthBalance(amount);

        balanceRepository.save(balance);

        log.info("Successfully authorized payment for account ID: {}", account.getId());
    }
}