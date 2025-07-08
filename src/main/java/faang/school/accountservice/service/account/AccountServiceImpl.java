package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.exceptions.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Transactional
    @Override
    public AccountDto create(AccountDto accountDto) {
        log.info("Creating account for user: {}, project: {}", accountDto.getUserId(), accountDto.getProjectId());

        AccountType accountType = determineAccountType(accountDto);
        Account account = accountMapper.toEntity(accountDto);
        account.setType(accountType);
        account.setStatus(AccountStatus.ACTIVE);

        freeAccountNumbersService.executeWithFreeAccountNumber(
                accountType,
                accountNumber -> {
                    account.setAccountNumber(accountNumber);
                    log.debug("Assigned account number: {} to account", accountNumber);
                }
        );

        Account savedAccount = accountRepository.save(account);
        log.info("Successfully created account with ID: {} and number: {}",
                savedAccount.getId(), savedAccount.getAccountNumber());

        return accountMapper.toDto(savedAccount);
    }

    @Transactional
    @Override
    public AccountDto update(Long id, AccountDto accountDto) {
        log.info("Updating account with ID: {}", id);

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Account not found with id: " + id));

        accountMapper.update(account, accountDto);

        Account updatedAccount = accountRepository.save(account);
        log.info("Successfully updated account with ID: {}", id);

        return accountMapper.toDto(updatedAccount);
    }

    @Transactional(readOnly = true)
    @Override
    public AccountDto getAccount(Long id) {
        log.debug("Retrieving account with ID: {}", id);

        return accountRepository.findById(id)
                .map(accountMapper::toDto)
                .orElseThrow(() -> new DataValidationException("Account not found with id: " + id));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        log.info("Deleting account with ID: {}", id);

        if (!accountRepository.existsById(id)) {
            throw new DataValidationException("Account not found with id: " + id);
        }

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Account not found with id: " + id));

        accountRepository.deleteById(id);

        try {
            freeAccountNumbersService.saveFreeAccountNumber(account.getType(), account.getAccountNumber());
            log.info("Returned account number {} to free pool for type {}",
                    account.getAccountNumber(), account.getType());
        } catch (Exception e) {
            log.warn("Failed to return account number {} to free pool: {}",
                    account.getAccountNumber(), e.getMessage());
        }

        log.info("Successfully deleted account with ID: {}", id);
    }

    private AccountType determineAccountType(AccountDto accountDto) {

        if (accountDto.getUserId() != null && accountDto.getProjectId() != null) {
            return AccountType.PERSONAL_PHYSICAL;
        } else if (accountDto.getUserId() != null) {
            return AccountType.PERSONAL_PHYSICAL;
        } else if (accountDto.getProjectId() != null) {
            return AccountType.CORPORATE;
        } else {
            throw new DataValidationException("Cannot determine account type: both userId and projectId are null");
        }
    }
}