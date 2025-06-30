package faang.school.accountservice.service;

import faang.school.accountservice.event.AccountCreatedEvent;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountPreviewDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.OwnerRequest;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.cache.AccountCacheableFetcher;
import faang.school.accountservice.service.utils.AccountServiceValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountCacheableFetcher accountFetcher;
    private final ApplicationEventPublisher eventPublisher;
    private final AccountServiceValidation accountServiceValidation;

    @Value("${cache.key.pagination.block-size}")
    private int blockSize;

    @Override
    @Transactional
    public AccountDto openAccount(CreateAccountDto createAccountDto) {
        log.info("Received request to open a new account for userId: {}", createAccountDto.getOwnerId());
        Account account = accountMapper.toEntity(createAccountDto);
        account.setStatus(AccountStatus.ACTIVE);
        Account savedAccount = accountRepository.save(account);

        eventPublisher.publishEvent(new AccountCreatedEvent(
                createAccountDto.getOwnerId(),createAccountDto.getOwnerType()
        ));
        log.info("Successfully opened account with id: {} for userId: {}",
                savedAccount.getId(), savedAccount.getOwnerId());
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public void freezeAccount(Long accountId) {
        log.info("Received request to freeze account with id: {}", accountId);
        updateAccountStatus(accountId, AccountStatus.ACTIVE, AccountStatus.FROZEN);
        log.info("Successfully frozen account with id: {}", accountId);
    }

    @Override
    @Transactional
    public void unfreezeAccount(Long accountId) {
        log.info("Received request to unfreeze account with id: {}", accountId);
        updateAccountStatus(accountId, AccountStatus.FROZEN, AccountStatus.ACTIVE);
        log.info("Successfully unfrozen account with id: {}", accountId);
    }

    @Override
    @Transactional
    public void closeAccount(Long accountId) {
        log.info("Received request to close account with id: {}", accountId);
        accountServiceValidation.validateAccount(accountId);
        Account account = findAccountById(accountId);
        accountServiceValidation.validateCloseAccount(accountId, account);
        account.setStatus(AccountStatus.CLOSED);
        eventPublisher.publishEvent(new AccountCreatedEvent(
                account.getOwnerId(),account.getOwnerType()
        ));
        log.info("Successfully closed account with id: {}", accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        log.info("Received request to get account with id: {}", id);
        Account account = findAccountById(id);
        return accountMapper.toDto(account);
    }

    @Override
    public List<AccountPreviewDto> findAccountsByOwner(OwnerRequest owner, Pageable pageable) {
        log.info("Received request to get accounts for ownerId: {}, ownerType: {}, pageable: {}",
                owner.getId(), owner.getType(), pageable);
        long startOffset = pageable.getOffset();
        long endOffset = startOffset + pageable.getPageSize();
        int startBlock = (int) (startOffset / blockSize);
        int endBlock = (int) (endOffset / blockSize);
        List<AccountPreviewDto> combinedAccounts = new ArrayList<>();
        for (int blockNum = startBlock; blockNum <= endBlock; blockNum++) {
            List<AccountPreviewDto> block = accountFetcher.fetchAccountBlock(owner, blockNum, pageable.getSort());
            if (block != null && !block.isEmpty()) {
                combinedAccounts.addAll(block);
            }
        }
        int startIndex = (int) (startOffset - (long) startBlock * blockSize);
        int endIndex = Math.min(startIndex + pageable.getPageSize(), combinedAccounts.size());
        return combinedAccounts.subList(startIndex, endIndex);
    }

    private Account findAccountById(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() ->
                new AccountNotFoundException(accountId));
    }

    private void updateAccountStatus(Long accountId, AccountStatus expectedStatus, AccountStatus newStatus) {
        accountServiceValidation.validateAccount(accountId);
        Account account = findAccountById(accountId);
        accountServiceValidation.hasPermission(account.getOwnerId());
        if (account.getStatus() != expectedStatus) {
            log.warn("Account with id: {} is not {}, current status: {}", accountId, expectedStatus.name(), account.getStatus());
            throw new DataValidationException(String.format("Account is not %s!", expectedStatus.name()));
        }
        account.setStatus(newStatus);
        eventPublisher.publishEvent(new AccountCreatedEvent(
                account.getOwnerId(), account.getOwnerType()
        ));
    }
}
