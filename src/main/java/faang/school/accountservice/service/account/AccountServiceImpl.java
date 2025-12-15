package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.dto.OpenAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.exception.AccessDeniedException;
import faang.school.accountservice.service.role.RoleService;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.AccountOperationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.config.properties.AccountProperties;
import faang.school.accountservice.entity.AccountBalance;
import faang.school.accountservice.repository.AccountBalanceRepository;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountBalanceRepository accountBalanceRepository;
    private final AccountMapper accountMapper;
    private final AccountProperties accountProperties;
    private final AccountNumberGenerator accountNumberGenerator;
    private final BalanceService balanceService;
    private final UserContext userContext;
    private final RoleService roleService;

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDto get(@NotNull Long id) {
        log.debug("Getting account by id: {}", id);
        return accountMapper.toDto(findAccountById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDto getByNumber(@NotBlank String number) {
        log.debug("Getting account by number");

        Account account = accountRepository.findByNumber(number)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return accountMapper.toDto(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponseDto> getByOwner(
            @NotNull Long ownerId,
            @NotNull OwnerType ownerType,
            @NotNull Pageable pageable) {

        validateAccess(ownerId);

        log.debug("Getting accounts for owner type: {}, page: {}", ownerType, pageable.getPageNumber());

        Page<Account> accounts = accountRepository.findByOwnerIdAndOwnerType(
                ownerId, ownerType, pageable);

        return accountMapper.toPageDto(accounts);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponseDto> getActiveByOwner(
            @NotNull Long ownerId,
            @NotNull OwnerType ownerType,
            @NotNull Pageable pageable) {

        log.debug("Getting active accounts for owner type: {}, page: {}",
                ownerType, pageable.getPageNumber());

        Page<Account> accounts = accountRepository.findByOwnerIdAndOwnerTypeAndStatus(
                ownerId, ownerType, AccountStatus.ACTIVE, pageable);

        return accountMapper.toPageDto(accounts);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountResponseDto> getActiveByOwnerAndCurrency(
            @NotNull Long ownerId,
            @NotNull OwnerType ownerType,
            @NotNull Currency currency,
            @NotNull Pageable pageable) {

        log.debug("Getting active accounts for owner type: {}, currency: {}, page: {}",
                ownerType, currency, pageable.getPageNumber());

        Page<Account> accounts = accountRepository.findByOwnerIdAndOwnerTypeAndCurrencyAndStatus(
                ownerId, ownerType, currency, AccountStatus.ACTIVE, pageable);

        return accountMapper.toPageDto(accounts);
    }

    @Override
    @Transactional
    public AccountResponseDto open(@Valid OpenAccountDto dto) {
        log.info("Opening account for owner type: {}", dto.getOwnerType());

        validateAccountLimit(dto.getOwnerId(), dto.getOwnerType());
        log.debug("Account limit validated");

        String accountNumber = resolveAccountNumber(dto.getNumber());
        log.debug("Account number resolved");

        Account account = createAccount(dto, accountNumber);
        Account savedAccount = accountRepository.save(account);

        createInitialBalance(savedAccount);

        log.info("Account opened successfully for owner type: {}, currency: {}",
                savedAccount.getOwnerType(), savedAccount.getCurrency());

        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public AccountResponseDto block(@NotNull Long id) {
        log.info("Blocking account with id: {}", id);

        Account account = findAccountById(id);
        account.block();

        log.info("Account blocked successfully");
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public AccountResponseDto unblock(@NotNull Long id) {
        log.info("Unblocking account with id: {}", id);

        Account account = findAccountById(id);
        account.unblock();

        log.info("Account unblocked successfully");
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public AccountResponseDto close(@NotNull Long id) {
        log.info("Closing account with id: {}", id);

        Account account = findAccountByIdWithLock(id);

        validateZeroBalance(id);
        log.debug("Balance validated (zero)");

        account.close(LocalDateTime.now());

        log.info("Account closed successfully");
        return accountMapper.toDto(account);
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    private Account findAccountByIdWithLock(Long id) {
        return accountRepository.findByIdWithLock(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }

    private void validateAccess(Long ownerId) {
        Long currentUserId = userContext.getUserId();
        boolean isAdmin = roleService.isAdmin(currentUserId);
        boolean isOwner = currentUserId != null && ownerId.equals(currentUserId);

        if (!isAdmin && !isOwner) {
            log.warn("Access denied for user {} to owner {} accounts", currentUserId, ownerId);
            throw new AccessDeniedException("Access denied. Only ADMIN role or account owner can access this resource.");
        }
    }

    private void validateAccountLimit(Long ownerId, OwnerType ownerType) {
        long activeAccountsCount = accountRepository.countByOwnerIdAndOwnerTypeAndStatus(
                ownerId, ownerType, AccountStatus.ACTIVE);

        int maxAccounts = accountProperties.getMaxActiveAccountsPerOwner();

        if (activeAccountsCount >= maxAccounts) {
            log.warn("Account limit reached for owner type: {}, current: {}, max: {}",
                    ownerType, activeAccountsCount, maxAccounts);
            throw new AccountOperationException(
                    "Maximum number of active accounts (" + maxAccounts + ") reached");
        }
    }

    private String resolveAccountNumber(String providedNumber) {
        if (providedNumber == null || providedNumber.isBlank()) {
            String generated = accountNumberGenerator.generate();
            log.debug("Generated new account number");
            return generated;
        }

        if (accountRepository.existsByNumber(providedNumber)) {
            log.warn("Attempt to create account with existing number");
            throw new AccountOperationException("Account with this number already exists");
        }

        log.debug("Using provided account number");
        return providedNumber;
    }

    private Account createAccount(OpenAccountDto dto, String accountNumber) {
        return Account.builder()
                .number(accountNumber)
                .ownerId(dto.getOwnerId())
                .ownerType(dto.getOwnerType())
                .type(dto.getType())
                .currency(dto.getCurrency())
                .status(AccountStatus.ACTIVE)
                .build();
    }

    private void validateZeroBalance(Long accountId) {
        
        BigDecimal balance = balanceService.getBalanceWithLock(accountId);

        if (balance.compareTo(BigDecimal.ZERO) != 0) {
            log.warn("Attempt to close account with non-zero balance");
            throw new AccountOperationException("Cannot close account with non-zero balance");
        }
    }

    private void createInitialBalance(Account account) {
        AccountBalance balance = new AccountBalance(account);
        accountBalanceRepository.save(balance);
    }
}