package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.dto.BalanceRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceAlreadyExistsException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static faang.school.accountservice.messages.ErrorMessages.ACCOUNT_NOT_FOUND;
import static faang.school.accountservice.messages.ErrorMessages.BALANCE_EXISTS_ERROR;
import static faang.school.accountservice.messages.ErrorMessages.BALANCE_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    @Override
    @Transactional
    @Retryable(
            value = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public BalanceResponseDto updateBalance(BalanceRequestDto request) {
        Balance balance = getBalance(request.getAccountNumber());

        balance.setAuthorizationBalance(request.getAuthorizationBalance());
        balance.setFactualBalance(request.getFactualBalance());
        log.info("Updating balance for account {}", request.getAccountNumber());
        balanceRepository.save(balance);
        return balanceMapper.toBalanceResponseDto(balance);
    }

    @Override
    @Transactional
    public BalanceResponseDto createBalance(BalanceRequestDto request) {
        Account account = getAccount(request.getAccountNumber());

        validateBalanceDoesNotExist(request, account);

        Balance balance = Balance.builder()
                .account(account)
                .authorizationBalance(request.getAuthorizationBalance())
                .factualBalance(request.getFactualBalance())
                .createdAt(LocalDateTime.now())
                .build();
        log.info("Creating balance for account {}", request.getAccountNumber());
        balanceRepository.save(balance);
        return balanceMapper.toBalanceResponseDto(balance);
    }

    @Override
    @Transactional
    public BalanceResponseDto getBalanceByAccountNumber(String accountNumber) {
        Balance balance = getBalance(accountNumber);
        BalanceResponseDto balanceResponseDto = balanceMapper.toBalanceResponseDto(balance);
        balanceResponseDto.setAccountNumber(accountNumber);
        log.info("Get BalanceDto: {}, accountNumber: {}", balanceResponseDto, accountNumber);
        return balanceResponseDto;
    }

    private Balance getBalance(String accountNumber) {
        return balanceRepository.findBalanceByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new BalanceNotFoundException(String.format(BALANCE_NOT_FOUND, accountNumber)));
    }

    private Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() ->
                        new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, accountNumber)));
    }

    private static void validateBalanceDoesNotExist(BalanceRequestDto request, Account account) {
        if (account.getAccountBalance() != null) {
            log.warn("Attempted to create a balance for account {} but it already exists", request.getAccountNumber());
            throw new BalanceAlreadyExistsException(String.format(BALANCE_EXISTS_ERROR, request.getAccountNumber()));
        }
    }
}
