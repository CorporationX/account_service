package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.AccountDepositException;
import faang.school.accountservice.exception.AccountWithdrawalException;
import faang.school.accountservice.exception.IllegalAccountAccessException;
import faang.school.accountservice.exception.InvalidAccountStatusException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.publisher.AccountEventPublisher;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountMapper accountMapper;
    private final AccountEventPublisher accountEventPublisher;
    private final AccountRepository accountRepository;

    public AccountDto createAccount(CreateAccountDto dto, Long ownerId) {
        Account account = generateNewAccount(dto, ownerId);

        accountRepository.save(account);

        AccountDto createdAccount = accountMapper.toDto(account);
        accountEventPublisher.publish(createdAccount);

        log.info("New account created: number: {}, owner: {}", account.getAccountNumber(), account.getOwnerName());
        return createdAccount;
    }

    public List<AccountDto> getAccounts(AccountOwnerType ownerType, Long ownerId) {
        List<Account> accounts = accountRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId);

        log.info("Request to get accounts of {} id: {} completed", ownerType, ownerId);
        return accountMapper.toDto(accounts);
    }
    @Transactional
    public AccountDto updateAccountStatus(String accountNumber, Long ownerId, AccountStatus accountStatus) {
        Account account = getAccountByNumber(accountNumber);

        validateAccountOwner(account, ownerId);
        validateAccountStatusCanBeChanged(account, accountStatus);

        account.setStatus(accountStatus);

        accountRepository.save(account);

        accountEventPublisher.publish(accountMapper.toDto(account));

        log.info("Account status updated: number: {}, status: {}", account.getAccountNumber(), accountStatus);
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountBalanceDto deposit(TransactionDto transactionDto) {
        Account account = getAccountByNumber(transactionDto.accountNumber());
        validateDepositPossibility(account.getStatus());

        BigDecimal transactionAmount = transactionDto.amount();
        BigDecimal newActualAmount = account.getBalance().getActualBalance().add(transactionAmount);
        account.getBalance().setActualBalance(newActualAmount);
        accountRepository.save(account);

        log.info("Account {} deposit of {} completed. New balance: {}", account.getAccountNumber(), transactionAmount, account.getBalance().getActualBalance());
        return createAccountBalanceDto(account);
    }

    @Transactional
    public AccountBalanceDto withdraw(Long ownerId, TransactionDto transactionDto) {
        Account account = getAccountByNumber(transactionDto.accountNumber());
        validateAccountOwner(account, ownerId);
        validateWithdrawalPossibility(account.getStatus());

        BigDecimal transactionAmount = transactionDto.amount();
        validateFundsAvailableForTransaction(account, transactionAmount);

        BigDecimal newActualAmount = account.getBalance().getActualBalance().subtract(transactionAmount);
        account.getBalance().setActualBalance(newActualAmount);

        BigDecimal newAuthorizedAmount = account.getBalance().getAuthorizedBalance().add(transactionAmount);
        account.getBalance().setAuthorizedBalance(newAuthorizedAmount);

        accountRepository.save(account);

        log.info("Account {} withdrawal of {} completed. New balance: {}", account.getAccountNumber(), transactionAmount, account.getBalance().getActualBalance());
        return createAccountBalanceDto(account);
    }

    @Transactional
    public void approve(TransactionDto transactionDto) {
        Account account = getAccountByNumber(transactionDto.accountNumber());

        BigDecimal transactionAmount = transactionDto.amount();

        BigDecimal newAuthorizedAmount = account.getBalance().getAuthorizedBalance().subtract(transactionAmount);
        account.getBalance().setAuthorizedBalance(newAuthorizedAmount);

        log.info("Transaction approved: account number: {}, amount: {}", account.getAccountNumber(), transactionAmount);
        accountRepository.save(account);
    }

    @Transactional
    public void cancel(TransactionDto transactionDto) {
        Account account = getAccountByNumber(transactionDto.accountNumber());

        BigDecimal transactionAmount = transactionDto.amount();

        BigDecimal newAuthorizedAmount = account.getBalance().getAuthorizedBalance().subtract(transactionAmount);
        account.getBalance().setAuthorizedBalance(newAuthorizedAmount);

        BigDecimal newActualAmount = account.getBalance().getActualBalance().add(transactionAmount);
        account.getBalance().setActualBalance(newActualAmount);

        log.info("Transaction canceled: account number: {}, amount: {}", account.getAccountNumber(), transactionAmount);
        accountRepository.save(account);
    }

    public AccountBalanceDto getAccountBalance(Long ownerId, String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        validateAccountOwner(account, ownerId);

        return createAccountBalanceDto(account);
    }

    private Account generateNewAccount(CreateAccountDto dto, Long ownerId) {
        Account account = accountMapper.toEntity(dto);
        account.setOwnerId(ownerId);
        account.setAccountNumber(generateAccountNumber());
        account.setStatus(AccountStatus.ACTIVE);

        Balance balance = new Balance();

        account.setBalance(balance);
        balance.setAccount(account);

        return account;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }

    private Account getAccountByNumber(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        if (account.isEmpty()) {
            throw new EntityNotFoundException(
                    String.format("Account with number %s doesn't exist", accountNumber)
            );
        }
        return account.get();
    }

    private void validateAccountOwner(Account account, Long ownerId) {
        if (!ownerId.equals(account.getOwnerId())) {
            throw new IllegalAccountAccessException(
                    String.format("Owner with id %d doesn't have access to the account %s", ownerId, account.getAccountNumber())
            );
        }
    }

    private void validateAccountStatusCanBeChanged(Account account, AccountStatus newStatus) {
        if (newStatus == account.getStatus()) {
            throw new InvalidAccountStatusException(
                    String.format("Account with number %s already has status %s", account.getAccountNumber(), account.getStatus())
            );
        }
    }

    private void validateDepositPossibility(AccountStatus status) {
        if (status == AccountStatus.DELETED) {
            throw new AccountDepositException("Deposit is not possible");
        }
    }

    private void validateWithdrawalPossibility(AccountStatus status) {
        if (status == AccountStatus.DELETED || status == AccountStatus.FROZEN || status == AccountStatus.INACTIVE) {
            throw new AccountWithdrawalException("Withdrawal is not possible");
        }
    }

    private void validateFundsAvailableForTransaction(Account account, BigDecimal transactionAmount) {
        if (account.getBalance().getActualBalance().compareTo(transactionAmount) < 0) {
            throw new AccountWithdrawalException(String.format(
                    "Not enough funds for the transaction. Available: %s, Transaction: %s", account.getBalance().getActualBalance(), transactionAmount));
        }
    }

    private AccountBalanceDto createAccountBalanceDto(Account account) {
        return new AccountBalanceDto(
                account.getAccountNumber(),
                account.getBalance().getActualBalance(),
                account.getBalance().getUpdatedAt()
        );
    }
}
