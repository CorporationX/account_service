package faang.school.accountservice.service.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.UpdateAccountDto;
import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.exception.IllegalAccountStatus;
import faang.school.accountservice.exception.IllegalAccountTypeForOwner;
import faang.school.accountservice.exception.NotUniqueAccountNumberException;
import faang.school.accountservice.filter.Filter;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.account_owner.AccountOwnerService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Positive;
import liquibase.hub.model.Project;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountOwnerService accountOwnerService;
    private final List<Filter<AccountFilterDto, Account>> filters;

    @Override
    public List<AccountDto> getAllAccounts(AccountFilterDto filterDto) {
        List<Account> filteredAccounts = filterAccounts(accountRepository.findAll(), filterDto);
        return accountMapper.toAccountDtos(filteredAccounts);
    }

    @Override
    public AccountDto getAccount(@Positive Long accountId) {
        return accountMapper.toAccountDto(getAccountById(accountId));
    }

    @Override
    public void updateAccount(UpdateAccountDto updateAccountDto) {
        Account account = getAccountById(updateAccountDto.accountId());
        updateStatus(account, updateAccountDto.status());
        if (updateAccountDto.currency() != null) {
            account.setCurrency(updateAccountDto.currency());
        }
        if (updateAccountDto.accountType() != null) {
            account.setAccountType(updateAccountDto.accountType());
        }
        accountRepository.save(account);
        log.info("Account {} updated", account.getId());
    }

    @Override
    public void createAccount(CreateAccountDto accountDto) {
        AccountOwner accountOwner = accountOwnerService.getAccountOwnerById(accountDto.getOwnerId());
        if (!accountDto.getAccountType().getAllowedOwnerTypes().contains(accountOwner.getOwnerType())) {
            throw new IllegalAccountTypeForOwner("Account type %s is not allowed for owner %s"
                    .formatted(accountDto.getAccountType(), accountOwner.getOwnerType()));
        }
        Account account = accountMapper.toAccount(accountDto);
        if (accountRepository.existsAccountByAccountNumber(accountDto.getAccountNumber())) {
            throw new NotUniqueAccountNumberException("Account with number %s is already exists"
                    .formatted(accountDto.getAccountNumber()));
        }
        account.setOwner(accountOwner);
        accountRepository.save(account);
        log.info("Create account {} with owner {}", account.getId(), account.getOwner().getOwnerId());
    }

    private void updateStatus(Account account, AccountStatus status) {
        if (status != null){
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
    }

    private List<Account> filterAccounts(List<Account> accounts, AccountFilterDto filterDto) {
        Stream<Account> accountStream = accounts.stream();
        return filters
                .stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(accountStream,
                        (stream, filter) -> filter.apply(stream, filterDto),
                        (s1, s2) -> s1)
                .toList();
    }

    private Account getAccountById(Long accountId) {
        return accountRepository
                .findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account with id %d not found".formatted(accountId)));
    }
}
