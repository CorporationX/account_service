package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.exception.EntityAlreadyBlockedException;
import faang.school.accountservice.exception.EntityAlreadyClosedException;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountValidator accountValidator;
    private final AccountMapper mapper;

    public AccountDto create(CreateAccountDto accountDto) {
        log.info("Creating a new account");
        accountValidator.validateDto(accountDto);

        Account account = mapper.toEntity(accountDto);
        account.setStatus(AccountStatus.ACTIVE);
        account.setNumber(generateAccountNumber());

        Account newAccount = accountRepository.save(account);
        log.info("New account was created: id={}", newAccount.getId());
        return mapper.toDto(newAccount);
    }


    @Transactional(readOnly = true)
    public AccountDto findById(UUID id) {
        return accountRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Account not found by id={}", id));
    }

    @Transactional(readOnly = true)
    public AccountDto findByNumber(String number) {
        return accountRepository.findByNumber(number)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Account not found by number={}", number));
    }

    @Transactional(readOnly = true)
    public List<AccountDto> findByUserId(long userId) {
        accountValidator.validateOwner(userId);
        return accountRepository.findByUserId(userId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccountDto> findByProjectId(long projectId) {
        accountValidator.validateOwner(projectId);
        return accountRepository.findByProjectId(projectId).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public void block(UUID id) {
        log.info("Blocking account by id={}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found by id={}", id));
        accountValidator.validateOwner(account.getUserId());
        accountValidator.validateOwner(account.getProjectId());

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new EntityAlreadyClosedException("Failed to block account id={}, it's already closed", id);
        }

        if (account.getStatus() == AccountStatus.FROZEN) {
            throw new EntityAlreadyBlockedException("Account id={}, is already blocked", id);
        }

        account.setStatus(AccountStatus.FROZEN);
    }

    @Transactional
    public void close(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found by id={}", id));
        accountValidator.validateOwner(account.getUserId());
        accountValidator.validateOwner(account.getProjectId());

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new EntityAlreadyClosedException("Account id={} is already closed", id);
        }

        account.setStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
    }

    private String generateAccountNumber() {
        return RandomStringUtils.randomNumeric(12, 20);
    }
}
