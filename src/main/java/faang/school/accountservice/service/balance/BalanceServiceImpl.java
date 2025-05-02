package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.dto.BalanceRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceAlreadyExistsException;
import faang.school.accountservice.exception.BalanceConflictException;
import faang.school.accountservice.exception.BalanceNotFoundException;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.BalanceRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static faang.school.accountservice.messages.ErrorMessages.ACCOUNT_NOT_FOUND;
import static faang.school.accountservice.messages.ErrorMessages.BALANCE_CONFLICT_ERROR;
import static faang.school.accountservice.messages.ErrorMessages.BALANCE_EXISTS_ERROR;
import static faang.school.accountservice.messages.ErrorMessages.BALANCE_NOT_FOUND;
import static faang.school.accountservice.messages.ErrorMessages.OPTIMISTIC_LOCK_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {
    private final BalanceRepository balanceRepository;
    private final AccountRepository accountRepository;
    private final BalanceMapper balanceMapper;

    @Override
    @Transactional
    public void updateBalance(BalanceRequestDto request) {
        try {
            Balance balance = getBalance(request.getAccountNumber());
            balance.setAuthorizationBalance(request.getAuthorizationBalance());
            balance.setFactualBalance(request.getFactualBalance());
            log.info("Updating balance for account {}", request.getAccountNumber());
            balanceRepository.save(balance);
        } catch (OptimisticLockException e) {
            log.error(OPTIMISTIC_LOCK_ERROR, e);
            throw new BalanceConflictException(BALANCE_CONFLICT_ERROR);
        }
    }

    @Override
    public void createBalance(BalanceRequestDto request) {
        Account account = getAccount(request.getAccountNumber());

        if (account.getBalance() != null) {
            log.warn("Attempted to create a balance for account {} but it already exists", request.getAccountNumber());
            throw new BalanceAlreadyExistsException(String.format(BALANCE_EXISTS_ERROR, request.getAccountNumber()));
        }

        Balance balance = new Balance();
        balance.setAccount(account);
        balance.setAuthorizationBalance(request.getAuthorizationBalance());
        balance.setFactualBalance(request.getFactualBalance());
        balance.setCreatedAt(LocalDateTime.now());
        log.info("Creating balance for account {}", request.getAccountNumber());
        balanceRepository.save(balance);
    }

    @Override
    public BalanceResponseDto getBalanceByAccountNumber(String accountNumber) {
        Balance balance = getBalance(accountNumber);
        BalanceResponseDto balanceResponseDto = balanceMapper.toBalanceDto(balance);
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
}
