package faang.school.accountservice.service;

import faang.school.accountservice.adapter.AccountRepositoryAdapter;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountFilterDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.specification.AccountSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountMapper accountMapper;
    private final AccountRepositoryAdapter accountRepositoryAdapter;

    @Override
    @Transactional
    public Account createAccount(AccountDto accountDto) {
        validateDataBeforeCreate(accountDto);
        Account account = accountMapper.toEntity(accountDto);
        account.setAccountStatus(AccountStatus.ACTIVE);
        log.info("Account with id {} was created.", account.getId());
        return accountRepositoryAdapter.save(account);
    }

    @Override
    @Transactional
    public Account blockAccount(Long id) {
        Account account = accountRepositoryAdapter.findById(id);
        if (account.getAccountStatus() == AccountStatus.FROZEN) {
            log.info("Account with id {} has already been blocked.", id);
            return account;
        }
        account.setAccountStatus(AccountStatus.FROZEN);
        log.info("Account with id {} was blocked.", id);
        return accountRepositoryAdapter.save(account);
    }

    @Override
    @Transactional
    public Account closeAccount(Long id) {
        Account account = accountRepositoryAdapter.findById(id);
        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            log.info("Account with id {} has already been closed.", id);
            return account;
        }
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        log.info("Account with id {} was closed.", id);
        return accountRepositoryAdapter.save(account);
    }

    @Override
    public List<AccountDto> getAccountsWithFilters(AccountFilterDto accountFilterDto) {
        Specification<Account> specification = Specification
                .where(AccountSpecification.hasNumber(accountFilterDto.getAccountNumber()))
                .or(AccountSpecification.hasOwner(accountFilterDto.getOwnerId()))
                .or(AccountSpecification.hasStatus(accountFilterDto.getAccountStatus()))
                .or(AccountSpecification.hasType(accountFilterDto.getType()));
        List<Account> accounts = accountRepositoryAdapter.findAll(specification);
        return accountMapper.toDto(accounts);
    }

    private void validateDataBeforeCreate(AccountDto accountDto) {
        if (accountRepositoryAdapter.existsByAccountNumberAndOwnerIdAndOwnerTypeAndType(accountDto.getAccountNumber(),
                accountDto.getOwnerId(), accountDto.getOwnerType(), accountDto.getType())) {
            throw new DataValidationException(String.format("The account %s with owner %s, owner type %s and type %s is existed",
                    accountDto.getAccountNumber(), accountDto.getOwnerId(), accountDto.getOwnerType(), accountDto.getType()));
        }
    }

}
