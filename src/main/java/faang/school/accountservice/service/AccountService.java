package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
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
        validateAccountStatus(account, accountStatus);

        account.setStatus(accountStatus);

        accountRepository.save(account);

        accountEventPublisher.publish(accountMapper.toDto(account));

        log.info("Account status updated: number: {}, status: {}", account.getAccountNumber(), accountStatus);
        return accountMapper.toDto(account);
    }

    private Account generateNewAccount(CreateAccountDto dto, Long ownerId) {
        Account account = accountMapper.toEntity(dto);
        account.setOwnerId(ownerId);
        account.setAccountNumber(generateAccountNumber());
        account.setStatus(AccountStatus.ACTIVE);
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

    private void validateAccountStatus(Account account, AccountStatus newStatus) {
        if (newStatus == account.getStatus()) {
            throw new InvalidAccountStatusException(
                    String.format("Account with number %s already has status %s", account.getAccountNumber(), account.getStatus())
            );
        }
    }
}
