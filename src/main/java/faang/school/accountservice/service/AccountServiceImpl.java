package faang.school.accountservice.service;

import faang.school.accountservice.client.UserServiceClient;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.CreatorValidationException;
import faang.school.accountservice.exception.OwnerValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.owners.OwnerValidator;
import faang.school.accountservice.repository.AccountRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final List<OwnerValidator> ownerValidators;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    @Override
    public List<AccountDto> getAccountByOwner(OwnerType ownerType, Long ownerId) {
        validateOwnerEntity(ownerType, ownerId);
        List<Account> accounts = accountRepository.findAllByOwnerTypeAndId(ownerType, ownerId)
                .stream()
                .filter(s -> !isAccountClosed(s.getId()))
                .toList();
        log.debug("Returning account list...");
        return accounts.stream().map(accountMapper::toAccountDto).toList();
    }

    @Override
    public AccountDto getAccountById(Long accountId) {
        Account searchedAccount = retrieveAndValidateAccount(accountId);
        log.debug("Returning account...");
        return accountMapper.toAccountDto(searchedAccount);
    }

    @Override
    public AccountDto openAccount(CreateAccountDto newAccountDto) {
        validateOwnerEntity(OwnerType.valueOf(newAccountDto.ownerType()), newAccountDto.ownerId());
        validateCreator(newAccountDto.creatorId());

        Account newAccount = accountMapper.toAccount(newAccountDto);
        newAccount.setNumber(generateAccountNumber());
        newAccount.setAccountStatus(AccountStatus.ACTIVE);
        newAccount.setBalance(BigDecimal.ZERO);
        Account savedAccount = accountRepository.save(newAccount);

        log.info("New account was created by {}", userContext.getUserId());
        return accountMapper.toAccountDto(savedAccount);
    }

    @Override
    @Transactional
    public void blockAccount(Long accountId) {
        try {
            Account blockedAccount = retrieveAndValidateAccount(accountId);
            blockedAccount.setAccountStatus(AccountStatus.BLOCKED);
            log.info("Account (number={}) got blocked by user {}",
                    blockedAccount.getNumber(), userContext.getUserId());
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock failed for blocking account {}", accountId);
            throw new ConcurrentModificationException("Account was modified concurrently", e);
        }
    }

    @Override
    @Transactional
    public void closeAccount(Long accountId) {
        try {
            Account closedAccount = retrieveAndValidateAccount(accountId);
            closedAccount.setAccountStatus(AccountStatus.CLOSED);
            closedAccount.setBalance(BigDecimal.ZERO);
            closedAccount.setClosedAt(LocalDateTime.now());
            log.info("Account (number={}) got closed by user {}",
                    closedAccount.getNumber(), userContext.getUserId());
        } catch (OptimisticLockException e) {
            log.warn("Optimistic lock failed for closing account {} ", accountId);
            throw new ConcurrentModificationException("Account was modified concurrently", e);
        }
    }

    private void validateOwnerEntity(OwnerType ownerType, Long ownerId) {
        OwnerValidator validator = ownerValidators.stream()
                .filter(s -> s.getOwnerType().equals(ownerType))
                .findFirst()
                .orElseThrow(UnsupportedOperationException::new);
        if (!validator.checkOwnerId(ownerId)) {
            throw new OwnerValidationException("Owner not found in the system");
        }
        log.debug("Owner {} found, proceeding with the operation", ownerId);
    }

    private void isAccountCreator(Long accountId) {
        Account account = accountRepository.findByIdOrThrow(accountId);
        if (!account.getCreatedBy().equals(userContext.getUserId())) {
            throw new OwnerValidationException("Only account creators are allowed to alter account data");
        }
        log.debug("User confirmed, proceeding with the operation");
    }

    private boolean isAccountClosed(Long accountId) {
        Account account = accountRepository.findByIdOrThrow(accountId);
        if (account.getAccountStatus().equals(AccountStatus.CLOSED)) {
            log.info("Account {} is closed and cannot be retrieved", accountId);
            return true;
        }
        return false;
    }

    private Account retrieveAndValidateAccount(Long accountId) {
        Account account = accountRepository.findByIdOrThrow(accountId);
        isAccountClosed(accountId);
        isAccountCreator(accountId);
        return account;
    }

    private void validateCreator(Long userId) {
        if (userServiceClient.getById(userId).username().isBlank()) {
            throw new CreatorValidationException("Creator user was not found in the system");
        }
    }

    //temporary
    private String generateAccountNumber() {
        int length = 12 + (int)(Math.random() * 9);

        StringBuilder sb = new StringBuilder();
        sb.append((int)(Math.random() * 9) + 1);

        for (int i = 1; i < length; i++) {
            sb.append((int)(Math.random() * 10));
        }

        return sb.toString();
    }
}
