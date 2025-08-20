package faang.school.accountservice.service;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.mapper.account.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.validator.account.AccountValidator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountValidator accountValidator;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountDto create(@NonNull CreateAccountDto createAccountDto) {
        accountValidator.validateCreate(createAccountDto);
        Account account = accountMapper.toAccount(createAccountDto);
        account.setStatus(AccountStatus.ACTIVE);
        return accountMapper.toAccountDto(accountRepository.save(account));
    }
}
