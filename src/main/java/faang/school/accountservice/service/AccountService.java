package faang.school.accountservice.service;

import faang.school.accountservice.converter.MapFieldsConverter;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.enums.PaymentStatus;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountOwnerService accountOwnerService;
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final BalanceService balanceService;
    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;
    private final MapFieldsConverter converter;
    private final RequestService requestService;
    private OwnerType ownerType;
    private AccountType accountType;
    private Currency currency;

    @Transactional(readOnly = true)
    public AccountDto getAccount(AccountDto accountDto) {
        Account account = getAccountForUpdate(accountDto.getId());
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto openAccount(RequestDto requestDto) {
        requestService.lockUser(requestDto.getId());
        ownerType = converter.convertToEnum(requestDto.getRequestData(), "ownerType", OwnerType.class);
        accountType = converter.convertToEnum(requestDto.getRequestData(), "accountType", AccountType.class);
        currency = converter.convertToEnum(requestDto.getRequestData(), "currency", Currency.class);
        AccountOwner accountOwner = accountOwnerService.getAndCreateIfNecessary(requestDto.getUserId(), ownerType);
        BigInteger accountNumber = freeAccountNumbersService.getFreeNumberAndDeleteFromCache(accountType);
        Balance balance = balanceService.createBalance();
        Account account = new Account(accountNumber,
                balance, accountOwner,
                AccountStatus.ACTIVE, accountType,currency);
        requestService.unlockUser(requestDto.getId());
        return accountMapper.toDto(accountRepository.save(account));
    }

    @Transactional
    @Retryable(backoff = @Backoff(delay = 300))
    public AccountDto suspendAccount(AccountDto accountDto) {
        Account account = getAccountForUpdate(accountDto.getId());
        if (!account.getAccountStatus().equals(AccountStatus.SUSPENDED) &&
                !account.getAccountStatus().equals(AccountStatus.CLOSED)) {
            account.setAccountStatus(AccountStatus.SUSPENDED);
            return accountMapper.toDto(accountRepository.save(account));
        } else {
            log.info("Account {} status is already SUSPENDED or CLOSED", account.getAccountNumber());
            throw new RuntimeException("Account status is already SUSPENDED or CLOSED");
        }
    }

    @Transactional
    @Retryable(backoff = @Backoff(delay = 300))
    public AccountDto closeAccount(AccountDto accountDto) {
        Account account = getAccountForUpdate(accountDto.getId());
        if (!account.getAccountStatus().equals(AccountStatus.CLOSED)) {
            account.setAccountStatus(AccountStatus.CLOSED);
            return accountMapper.toDto(accountRepository.save(account));
        } else {
            log.info("Account {} status is already CLOSED", account.getAccountNumber());
            throw new RuntimeException("Account status is already CLOSED");
        }
    }

    @Async("balanceOperationExecutor")
    public CompletableFuture<PaymentStatus> transferToAuthorizedBalance(Long accountId, BigDecimal sum) {
        Account account = getAccountForUpdate(accountId);
        return CompletableFuture.completedFuture(balanceService
                .transferToAuthorizedBalance(account.getId(),
                        account.getBalance(),
                        sum));
    }

    @Async("balanceOperationExecutor")
    public CompletableFuture<PaymentStatus> receiveFunds(Long accountId, BigDecimal sum) {
        Account account = getAccountForUpdate(accountId);
        return CompletableFuture.completedFuture(balanceService
                .receiveFunds(account.getId(),
                        account.getBalance(),
                        sum));
    }

    @Async("balanceOperationExecutor")
    public CompletableFuture<PaymentStatus> spendFromAuthorizedBalance(Long accountId, BigDecimal sum) {
        Account account = getAccountForUpdate(accountId);
        return CompletableFuture.completedFuture(balanceService
                .spendFromAuthorizedBalance(account.getId(),
                        account.getBalance(),
                        sum));
    }

    @Async("balanceOperationExecutor")
    public CompletableFuture<PaymentStatus> processPayment(Long accountId, BigDecimal sum) {
        Account account = getAccountForUpdate(accountId);
        return CompletableFuture.completedFuture(balanceService.processPayment(account.getId(),
                account.getBalance(),
                sum));
    }

    @Async
    public void createRequest(RequestDto requestDto){
        requestService.createRequest(requestDto);
    }

    public boolean existsById(UUID id){
        return requestService.existsById(id);
    }

    public boolean existsByUserLock(Long userId, Long lockUser){
        return requestService.existsByUserLock(userId, lockUser);
    }

    private Account getAccountForUpdate(long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> {
            log.info("Account with id {} not found", accountId);
            return new EntityNotFoundException("Account with id " + accountId + " not found");
        });
    }

}
