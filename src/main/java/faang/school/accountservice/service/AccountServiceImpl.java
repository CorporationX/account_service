package faang.school.accountservice.service;

import faang.school.accountservice.adapter.AccountRepositoryAdapter;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountFilterDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.mapper.AccountMapper;
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
    public AccountDto createAccount(AccountDto accountDto) {
        validateDataBeforeCreate(accountDto);
        Account account = accountMapper.toEntity(accountDto);
        account.setAccountStatus(AccountStatus.ACTIVE);
        log.info("Account with id {} was created.", account.getId());
        return accountMapper.toDto(accountRepositoryAdapter.save(account));
    }

    @Override
    @Transactional
    public AccountDto blockAccount(Long id) {
        Account account = accountRepositoryAdapter.findById(id);
        if (account.getAccountStatus() == AccountStatus.FROZEN) {
            log.info("Account with id {} has already been blocked.", id);
            return accountMapper.toDto(account);
        }
        account.setAccountStatus(AccountStatus.FROZEN);
        log.info("Account with id {} was blocked.", id);
        return accountMapper.toDto(accountRepositoryAdapter.save(account));
    }

    @Override
    @Transactional
    public AccountDto closeAccount(Long id) {
        Account account = accountRepositoryAdapter.findById(id);
        if (account.getAccountStatus() == AccountStatus.CLOSED) {
            log.info("Account with id {} has already been closed.", id);
            return accountMapper.toDto(account);
        }
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        log.info("Account with id {} was closed.", id);
        return accountMapper.toDto(accountRepositoryAdapter.save(account));
    }

    @Override
    public List<AccountDto> getAccountsWithFilters(AccountFilterDto accountFilterDto) {
        Specification<Account> specification = Specification.where(null);
        if (accountFilterDto.getAccountNumber() != null) {
            specification = specification.and(AccountSpecification.hasNumber(accountFilterDto.getAccountNumber()));
        }
        if (accountFilterDto.getOwnerType() != null) {
            specification = specification.and(AccountSpecification.hasOwnerType(accountFilterDto.getOwnerType()));
        }
        if (accountFilterDto.getOwnerId() != null) {
            specification = specification.and(AccountSpecification.hasOwner(accountFilterDto.getOwnerId()));
        }
        if (accountFilterDto.getAccountStatus() != null) {
            specification = specification.and(AccountSpecification.hasStatus(accountFilterDto.getAccountStatus()));
        }
        if (accountFilterDto.getType() != null) {
            specification = specification.and(AccountSpecification.hasType(accountFilterDto.getType()));
        }
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
