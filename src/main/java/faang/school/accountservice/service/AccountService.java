package faang.school.accountservice.service;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Status;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.AccountValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AccountValidator accountValidator;
    private final FreeAccountNumbersService freeAccountNumbersService;

    @Transactional(readOnly = true)
    public AccountDto getAccount(Long id) {
        Account account = getById(id);
        return accountMapper.toDto(account);
    }

    @Transactional
    public AccountDto openAccount(AccountDto accountDto) {
        accountValidator.initValidation(accountDto);
        Account account = accountMapper.toEntity(accountDto);

        freeAccountNumbersService.getAndHandleAccountNumber(account.getType(),
                (freeAccountNumber -> {
                    account.setNumber(String.valueOf(freeAccountNumber.getAccount_number()));
                }));
        account.setStatus(Status.ACTIVE);
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDto(savedAccount);
    }

    @Transactional
    public void blockAccount(long id) {
        Account account = getById(id);
        account.setStatus(Status.FROZEN);
    }

    @Transactional
    public void closeAccount(long id) {
        Account account = getById(id);
        account.setStatus(Status.CLOSED);
    }

    @Transactional
    public void activateAccount(long id) {
        Account account = getById(id);
        account.setStatus(Status.ACTIVE);
    }

    private Account getById(long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity account with id " + id + " not found"));
    }
}
