package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceRepository balanceRepository;
    private final BalanceMapper balanceMapper;

    @Transactional
    public void createBalance(Account account) {
        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setActualBalance(new BigDecimal(0));
        balance.setAuthorizationBalance(new BigDecimal(0));
        balanceRepository.save(balance);
        log.info("Created account balance: {}", account);
    }

    @Transactional(readOnly = true)
    public BalanceDto getBalance(long balanceId) {
        Balance balance = getBalanceById(balanceId);
        log.info("Account balance sent: {}", balance.getAccount().getNumber());
        return balanceMapper.toDto(balance);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public BalanceDto writeOffClearingBalance(BalanceDto balanceDto) {
        Balance updatedBalance = balanceMapper.toEntity(balanceDto);
        Balance balance = getBalanceByNumber(balanceDto.getAccountNumber());
        balance.setActualBalance(balance.getActualBalance().subtract(updatedBalance.getActualBalance()));
        log.info("Account balance: {} update clearing", balance.getAccount().getNumber());
        return balanceMapper.toDto(balance);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public BalanceDto writeOffAuthorizationBalance(BalanceDto balanceDto) {
        Balance holdBalance = balanceMapper.toEntity(balanceDto);
        Balance balance = getBalanceByNumber(balanceDto.getAccountNumber());
        BigDecimal authorizationBalance = balance.getAuthorizationBalance();
        BigDecimal newAuthorizationBalance = holdBalance.getAuthorizationBalance();

        if (authorizationBalance.compareTo(newAuthorizationBalance) >= 0) {
            balance.setAuthorizationBalance(authorizationBalance.subtract(newAuthorizationBalance));
        } else if (authorizationBalance.compareTo(newAuthorizationBalance) < 0) {
            throw new IllegalArgumentException("The amount of funds in the account is insufficient for debiting");
        }
        balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    @Retryable(retryFor = OptimisticLockingFailureException.class, backoff = @Backoff(delay = 3000L))
    public BalanceDto addingToBalance(BalanceDto balanceDto) {
        Balance updatedBalance = balanceMapper.toEntity(balanceDto);
        BigDecimal updateActualBalance = updatedBalance.getActualBalance();
        Balance balance = getBalanceByNumber(balanceDto.getAccountNumber());

        balance.setActualBalance(balance.getActualBalance().add(updateActualBalance));
        balance.setAuthorizationBalance(balance.getAuthorizationBalance().add(updateActualBalance));
        balanceRepository.save(balance);
        return balanceMapper.toDto(balance);
    }

    @Transactional
    public void comparisonOfBalances(String number) {
        Balance balance = getBalanceByNumber(number);
        balance.setAuthorizationBalance(balance.getActualBalance());
        balanceRepository.save(balance);
    }

    private Balance getBalanceById(long id) {
        return balanceRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Balance with the specified id: " + id + " not found"));
    }

    private Balance getBalanceByNumber(String number) {
        return balanceRepository.findByAccountNumber(number);
    }
}
