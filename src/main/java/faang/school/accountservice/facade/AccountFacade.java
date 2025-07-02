package faang.school.accountservice.facade;

import faang.school.accountservice.dto.account.AccountCreateProjectDto;
import faang.school.accountservice.dto.account.AccountCreateUserDto;
import faang.school.accountservice.dto.account.ResponseAccountDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountFacade {

    private final AccountService accountService;
    private final AccountMapper accountMapper;

    public ResponseAccountDto createAccountForUser(AccountCreateUserDto accountDto) {
        Account account = accountMapper.toAccount(accountDto);

        Account createdAccount = accountService.createAccount(account);

        ResponseAccountDto responseDto = accountMapper.toResponseAccountDto(createdAccount);

        return responseDto;
    }

    public ResponseAccountDto createAccountForProject(AccountCreateProjectDto accountDto) {
        Account account = accountMapper.toAccount(accountDto);

        Account createdAccount = accountService.createAccount(account);

        ResponseAccountDto responseDto = accountMapper.toResponseAccountDto(createdAccount);

        return responseDto;
    }

    public ResponseAccountDto getAccountById(UUID accountId) {
        Account createdAccount = accountService.getAccountById(accountId);

        ResponseAccountDto responseDto = accountMapper.toResponseAccountDto(createdAccount);

        return responseDto;
    }

    public ResponseAccountDto getAccountByNumber(String accountNumber) {
        Account createdAccount = accountService.getAccountByNumber(accountNumber);

        ResponseAccountDto responseDto = accountMapper.toResponseAccountDto(createdAccount);

        return responseDto;
    }

    public ResponseAccountDto closeAccount(UUID accountId) {
        Account closedAccount = accountService.closeAccount(accountId);

        ResponseAccountDto responseDto = accountMapper.toResponseAccountDto(closedAccount);

        return responseDto;
    }

    public ResponseAccountDto convertAccountCurrency(UUID accountId, Currency currency) {
        Account updatedAccount = accountService.convertAccountCurrency(accountId, currency);

        ResponseAccountDto responseDto = accountMapper.toResponseAccountDto(updatedAccount);

        return responseDto;
    }
}
