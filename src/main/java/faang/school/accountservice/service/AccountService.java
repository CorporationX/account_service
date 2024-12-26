package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceChangeDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.dto.TransactionRequestDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.Transaction;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.TransactionStatus;
import faang.school.accountservice.enums.TransactionType;
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
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountMapper accountMapper;
    private final AccountEventPublisher accountEventPublisher;
    private final TransactionService transactionService;
    private final AccountRepository accountRepository;

    @Transactional
    public AccountDto createAccount(CreateAccountDto dto, Long ownerId) {
        Account account = generateNewAccount(dto, ownerId);

        accountRepository.save(account);

        AccountDto createdAccount = accountMapper.toDto(account);
        accountEventPublisher.publish(createdAccount);

        log.debug("New account created: number: {}, owner: {}", account.getAccountNumber(), account.getOwnerName());
        return createdAccount;
    }

    public List<AccountDto> getAccounts(AccountOwnerType ownerType, Long ownerId) {
        List<Account> accounts = accountRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId);

        log.debug("Request to get accounts of {} id: {} completed", ownerType, ownerId);
        return accountMapper.toDto(accounts);
    }

    @Transactional
    public AccountDto updateAccountStatus(String accountNumber, Long ownerId, AccountStatus accountStatus) {
        Account account = getAccountByNumber(accountNumber);

        validateAccountOwner(account, ownerId);
        validateAccountStatusCanBeChanged(account, accountStatus);

        account.setStatus(accountStatus);

        accountRepository.save(account);

        AccountDto accountDto = accountMapper.toDto(account);

        accountEventPublisher.publish(accountDto);

        log.debug("Account status updated: number: {}, status: {}", account.getAccountNumber(), accountStatus);
        return accountMapper.toDto(account);
    }

    @Transactional
    public void deleteAccount(String accountNumber, Long ownerId) {
        Account account = getAccountByNumber(accountNumber);
        validateAccountOwner(account, ownerId);
        accountRepository.deleteById(account.getId());
        log.debug("Account deleted: number: {}", account.getAccountNumber());
    }

    @Transactional
    public BalanceChangeDto deposit(TransactionRequestDto transactionRequestDto) {
        Account account = getAccountByNumber(transactionRequestDto.accountNumber());

        BigDecimal amount = transactionRequestDto.amount();

        increaseActualBalance(account, amount);

        createDepositTransaction(account, amount);

        accountRepository.save(account);

        log.info("Account {} deposit of {} completed. New balance: {}", account.getAccountNumber(), amount, account.getBalance().getActualBalance());
        return createBalanceChangeDto(account, amount);
    }

    @Transactional
    public BalanceChangeDto withdraw(Long ownerId, TransactionRequestDto transactionRequestDto) {
        Account account = getAccountByNumber(transactionRequestDto.accountNumber());
        validateAccountOwner(account, ownerId);
        validateWithdrawalPossibility(account);

        BigDecimal amount = transactionRequestDto.amount();
        validateFundsAvailableForTransaction(account, amount);

        decreaseActualBalance(account, amount);

        increaseAuthorizedBalance(account, amount);

        createWithdrawalTransaction(account, amount);

        accountRepository.save(account);

        log.info("Account {} withdrawal of {} completed. New balance: {}", account.getAccountNumber(), amount, account.getBalance().getActualBalance());
        return createBalanceChangeDto(account, amount);
    }

    @Transactional
    public void approve(TransactionRequestDto transactionRequestDto, Long transactionId) {
        BigDecimal amount = transactionRequestDto.amount();

        transactionService.validateTransactionExists(transactionId, amount);

        Account account = getAccountByNumber(transactionRequestDto.accountNumber());

        decreaseAuthorizedBalance(account, amount);

        transactionService.approveTransaction(transactionId);

        log.info("Transaction approved: account number: {}, amount: {}", account.getAccountNumber(), amount);
        accountRepository.save(account);
    }

    @Transactional
    public void reject(TransactionRequestDto transactionRequestDto, Long transactionId) {
        BigDecimal amount = transactionRequestDto.amount();

        transactionService.validateTransactionExists(transactionId, amount);

        Account account = getAccountByNumber(transactionRequestDto.accountNumber());

        decreaseAuthorizedBalance(account, amount);

        increaseActualBalance(account, amount);

        transactionService.rejectTransaction(transactionId);

        log.info("Transaction canceled: account number: {}, amount: {}", account.getAccountNumber(), amount);
        accountRepository.save(account);
    }

    public AccountBalanceDto getAccountBalance(Long ownerId, String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        validateAccountOwner(account, ownerId);

        return new AccountBalanceDto(
                account.getAccountNumber(),
                account.getBalance().getActualBalance(),
                account.getBalance().getUpdatedAt());
    }

    @Transactional
    public List<TransactionDto> getTransactions(Long ownerId, String accountNumber) {
        Account account = getAccountByNumber(accountNumber);
        validateAccountOwner(account, ownerId);

        return account.getTransactions().stream()
                .filter(transaction -> transaction.getTransactionStatus() == TransactionStatus.APPROVED ||
                        transaction.getTransactionStatus() == TransactionStatus.PENDING)
                .map(accountMapper::toDto)
                .toList();
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
        String accountNumber;
        do {
            accountNumber = ThreadLocalRandom.current()
                    .ints(20, 0, 10)
                    .mapToObj(String::valueOf)
                    .collect(Collectors.joining());
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
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

    private void validateWithdrawalPossibility(Account account) {
        if (account.getStatus() == AccountStatus.FROZEN || account.getStatus() == AccountStatus.INACTIVE) {
            throw new AccountWithdrawalException(String.format(
                    "Withdrawal from account %s is not possible. Status: %s", account.getAccountNumber(), account.getStatus()));
        }
    }

    private void validateFundsAvailableForTransaction(Account account, BigDecimal transactionAmount) {
        if (account.getBalance().getActualBalance().compareTo(transactionAmount) < 0) {
            throw new AccountWithdrawalException(String.format(
                    "Not enough funds for the transaction. Available: %s, Transaction: %s", account.getBalance().getActualBalance(), transactionAmount));
        }
    }

    private void increaseActualBalance(Account account, BigDecimal transactionAmount) {
        BigDecimal newActualAmount = account.getBalance().getActualBalance().add(transactionAmount);
        account.getBalance().setActualBalance(newActualAmount);
    }

    private void decreaseActualBalance(Account account, BigDecimal transactionAmount) {
        BigDecimal newActualAmount = account.getBalance().getActualBalance().subtract(transactionAmount);
        account.getBalance().setActualBalance(newActualAmount);
    }

    private void increaseAuthorizedBalance(Account account, BigDecimal transactionAmount) {
        BigDecimal newAuthorizedAmount = account.getBalance().getAuthorizedBalance().add(transactionAmount);
        account.getBalance().setAuthorizedBalance(newAuthorizedAmount);
    }

    private void decreaseAuthorizedBalance(Account account, BigDecimal transactionAmount) {
        BigDecimal newAuthorizedAmount = account.getBalance().getAuthorizedBalance().subtract(transactionAmount);
        account.getBalance().setAuthorizedBalance(newAuthorizedAmount);
    }

    private BalanceChangeDto createBalanceChangeDto(Account account, BigDecimal amount) {
        Transaction transaction = account.getTransactions().get(account.getTransactions().size() - 1);
        return new BalanceChangeDto(
                transaction.getId(),
                transaction.getTransactionType(),
                amount,
                account.getBalance().getUpdatedAt(),
                account.getBalance().getActualBalance()
        );
    }

    private void createDepositTransaction(Account account, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionAmount(amount)
                .transactionType(TransactionType.DEPOSIT)
                .transactionStatus(TransactionStatus.APPROVED)
                .build();
        account.getTransactions().add(transaction);
    }

    private void createWithdrawalTransaction(Account account, BigDecimal amount) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionAmount(amount)
                .transactionType(TransactionType.WITHDRAWAL)
                .transactionStatus(TransactionStatus.PENDING)
                .build();
        account.getTransactions().add(transaction);
    }
}
