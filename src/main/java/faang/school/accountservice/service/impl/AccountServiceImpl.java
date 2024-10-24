package faang.school.accountservice.service.impl;

import faang.school.accountservice.config.generator.AccountNumberGenerator;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.dto.AccountDto;
import faang.school.accountservice.model.entity.Account;
import faang.school.accountservice.model.enums.AccountStatus;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.validator.AccountValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;
    private final AccountNumberGenerator accountNumberGenerator;

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> getAllAccountsByOwnerId(Long id) {
        List<Account> allByOwnerId = accountRepository.findAllByOwnerId(id);
        return accountMapper.toDtoList(allByOwnerId);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto getAccountById(Long id) {
        Account account = findAccountById(id);
        return accountMapper.toDto(account);
    }

    @Override
    @Transactional
    public AccountDto openAccount(long ownerId, AccountDto dto) {
        String accountNumber = accountNumberGenerator.generateAccountNumber(); // Использую пока нет таски с генерацией
        Account account = accountMapper.toEntity(dto);
        account.setAccountNumber(accountNumber);
        account.setOwnerId(ownerId);
        account.setOwnerType(dto.ownerType());
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Override
    @Transactional
    public void blockAccount(long ownerId, Long id) {
        Account account = findAccountById(id);
        accountValidator.validateAccountToBlock(account, ownerId);
        account.setUpdatedAt(LocalDateTime.now());
        account.setAccountStatus(AccountStatus.BLOCKED);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void closeAccount(long ownerId, Long id) {
        Account account = findAccountById(id);
        accountValidator.validateAccountToClose(account, ownerId);
        // проверка на то, что баланс положительный
        account.setUpdatedAt(LocalDateTime.now());
        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        accountRepository.save(account);
    }

    private Account findAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with ID: %d not found"
                        .formatted(id)));
    }
}
