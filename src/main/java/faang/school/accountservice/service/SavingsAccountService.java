package faang.school.accountservice.service;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.entity.SavingsAccount;
import faang.school.accountservice.exception.EntityAlreadyExistsException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.SavingsAccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.repository.SavingsAccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingsAccountService {
    private final SavingsAccountRepository savingsAccountRepository;
    private final AccountRepository accountRepository;
    private final TariffService tariffService;
    private final SavingsAccountMapper mapper;
    private final AccountValidator accountValidator;

    @Transactional
    public SavingsAccountDto create(UUID accountId, long tariffId) {
        Account account = findAccountById(accountId);
        accountValidator.validateAccountStatus(account);
        checkIfSavingsExists(account);

        SavingsAccount savingsAccount = createSavingsAccountWithTariff(account, tariffId);

        savingsAccount = savingsAccountRepository.save(savingsAccount);
        return mapper.toDto(savingsAccount);
    }

    @Transactional(readOnly = true)
    public SavingsAccountDto findById(UUID id) {
        return savingsAccountRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Savings account not found by id = {}", id));
    }

    @Transactional(readOnly = true)
    public SavingsAccountDto findByUserId(Long id) {
        accountValidator.validateOwner(id);
        return savingsAccountRepository.findByUserId(id).stream()
                .findFirst()
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Savings account not found by userId = {}", id));
    }

    @Transactional(readOnly = true)
    public SavingsAccountDto findByProjectId(Long id) {
        accountValidator.validateOwner(id);
        return savingsAccountRepository.findByProjectId(id).stream()
                .findFirst()
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Savings account not found by projectId = {}", id));
    }

    private Account findAccountById(UUID id) {
        log.debug("Finding account by id = {}", id);
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Account not found by id = {}", id));
    }

    private void checkIfSavingsExists(Account account) {
        log.debug("Checking if savings account by id = {} already exists", account.getId());
        boolean isExistsSavings = savingsAccountRepository.existsByOwnerId(account.getUserId(), account.getProjectId());
        if (isExistsSavings) {
            throw new EntityAlreadyExistsException(
                    "User id = {} or project id = {} already has an open savings account id = {}",
                    account.getUserId(), account.getProjectId(), account.getId());
        }
    }

    private SavingsAccount createSavingsAccountWithTariff(Account account, long tariffId) {
        log.debug("Creating savings account with id = {} and tariffId = {}", account.getId(), tariffId);
        return SavingsAccount.builder()
                .account(account)
                .balance(BigDecimal.ZERO)
                .tariffs(List.of(tariffService.createTariffHistory(account, tariffId)))
                .build();
    }
}
