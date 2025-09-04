package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Account;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserContext userContext;
    private final AccountMapper accountMapper;
    private final FreeAccountNumbersService freeAccountNumbersService;
    private final AccountRepository accountRepository;

    public AccountDto create(CreateAccountDto createAccountDto) {
        long userId = userContext.getUserId();
        AccountType accountType = createAccountDto.accountType();
        Account account = accountMapper.toAccount(createAccountDto);
        account.setUserId(userId);

        // В лямбду, например, можно передать метод, который создаст запись в
        // таблице account с новым полученным номером счёта.
        try {
            freeAccountNumbersService.retrieveAccountNumber(accountType,
                    freeAccountNumber -> {
                        long accountNumber = freeAccountNumber.getId().getAccountNumber();
                        account.setAccountNumber(accountNumber);
                        accountRepository.save(account);
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return accountMapper.toAccountDto(account);
    }
}
