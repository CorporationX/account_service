package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.model.account.Account;
import faang.school.accountservice.model.owner.OwnerType;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final AccountMapper accountMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto createAccount(@RequestBody @Valid AccountDto accountDto) {
        Account account = accountService.createAccount(accountMapper.toAccountEntity(accountDto));
        return accountMapper.toAccountDto(account);
    }

    @GetMapping
    public List<AccountDto> getAccount(@RequestParam QueryType query,
                                       @RequestParam(required = false) String number,
                                       @RequestParam(required = false) Long externalId) {
        List<Account> accounts = switch (query) {
            case NUMBER -> List.of(accountService.getAccountByNumber(number));
            case USER -> accountService.getAccountsByOwner(externalId, OwnerType.USER);
            case PROJECT -> accountService.getAccountsByOwner(externalId, OwnerType.PROJECT);
        };

        return accountMapper.toAccountDtoList(accounts);
    }

    @PutMapping("/suspend")
    public AccountDto freezeAccount(@RequestParam String number) {
        Account account = accountService.suspendAccount(number);
        return accountMapper.toAccountDto(account);
    }

    @PutMapping("/activate")
    public AccountDto unfreezeAccount(@RequestParam String number) {
        Account account = accountService.activateAccount(number);
        return accountMapper.toAccountDto(account);
    }

    @PutMapping("/close")
    public AccountDto closeAccount(@RequestParam String number) {
        Account account = accountService.closeAccount(number);
        return accountMapper.toAccountDto(account);
    }
}
