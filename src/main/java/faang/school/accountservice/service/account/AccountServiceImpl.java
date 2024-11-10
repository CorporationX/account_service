package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.UpdateAccountDto;
import faang.school.accountservice.dto.account_owner.AccountOwnerDto;
import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.exception.IllegalAccountStatus;
import faang.school.accountservice.exception.IllegalAccountTypeForOwner;
import faang.school.accountservice.exception.NotUniqueAccountNumberException;
import faang.school.accountservice.filter.Filter;
import faang.school.accountservice.filter.SpecsUtils;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account_owner.AccountOwnerService;
import faang.school.accountservice.validation.AccountOwnerValidator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountOwnerService accountOwnerService;
    private final AccountOwnerValidator accountOwnerValidator;
    private final List<Filter<AccountFilterDto, Account>> filters;

    @Override
    public Page<AccountDto> getAccounts(AccountFilterDto filterDto, int page, int size) {
        accountOwnerValidator.validateOwnerByAccountOwnerId(filterDto.ownerId());
        Pageable pageable = PageRequest.of(page, size);
        Specification<Account> filterSpecification =
                SpecsUtils.combineSpecsWithAndConditionFromFilters(filters, filterDto);
        Page<Account> accounts =
                accountRepository.findAll(filterSpecification, pageable);
        return new PageImpl<>(
                accountMapper.toAccountDtos(accounts.get()),
                pageable,
                accounts.getTotalElements()
        );
    }

    @Override
    public AccountDto getAccount(@Positive Long accountId) {
        Account account = getAccountById(accountId);
        accountOwnerValidator.validateOwner(account.getOwner());
        return accountMapper.toAccountDto(account);
    }

    @Override
    public AccountDto updateAccount(Long accountId, UpdateAccountDto updateAccountDto) {
        Account account = getAccountById(accountId);
        accountOwnerValidator.validateOwner(account.getOwner());
        updateStatus(account, updateAccountDto.status());
        if (updateAccountDto.currency() != null) {
            account.setCurrency(updateAccountDto.currency());
        }
        if (updateAccountDto.accountType() != null) {
            checkAllowedOwnerType(updateAccountDto.accountType(), account.getOwner().getOwnerType());
            account.setAccountType(updateAccountDto.accountType());
        }
        accountRepository.save(account);
        log.info("Account {} updated", account.getId());
        return accountMapper.toAccountDto(account);
    }

    @Override
    public AccountDto createAccount(CreateAccountDto accountDto) {
        AccountOwnerDto ownerDto = accountDto.owner();
        accountOwnerValidator.validateOwnerByOwnerIdAndType(ownerDto.ownerId(), ownerDto.ownerType());
        AccountOwner accountOwner =
                accountOwnerService.getOrCreateAccountOwner(ownerDto.ownerId(), ownerDto.ownerType());
        checkAllowedOwnerType(accountDto.accountType(), accountOwner.getOwnerType());
        Account account = accountMapper.toAccount(accountDto);
        if (accountRepository.existsAccountByAccountNumber(accountDto.accountNumber())) {
            throw new NotUniqueAccountNumberException("Account with number %s is already exists"
                    .formatted(accountDto.accountNumber()));
        }
        account.setOwner(accountOwner);
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
        log.info("Create account {} with owner {}", account.getId(), account.getOwner().getOwnerId());
        return accountMapper.toAccountDto(account);
    }

    private void updateStatus(Account account, AccountStatus status) {
        if (status == null) {
            return;
        }
        if (account.getStatus().equals(status)) {
            throw new IllegalAccountStatus("Can't change account %d with status %s to status %s"
                    .formatted(account.getId(), account.getStatus(), status));
        }
        account.setStatus(status);
        if (account.getStatus() == AccountStatus.CLOSED) {
            account.setClosedAt(LocalDateTime.now());
        } else {
            account.setClosedAt(null);
        }
    }

    private void checkAllowedOwnerType(AccountType accountType, OwnerType ownerType) {
        if (!accountType.getAllowedOwnerTypes().contains(ownerType)) {
            throw new IllegalAccountTypeForOwner("Account type %s is not allowed for owner type %s"
                    .formatted(accountType, ownerType));
        }
    }

    private Account getAccountById(Long accountId) {
        return accountRepository
                .findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account with id %d not found".formatted(accountId)));
    }
}
