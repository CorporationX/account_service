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
    public AccountDto getAccountByNumber(@RequestParam String number) {
        Account account = accountService.getAccountByNumber(number);
        return accountMapper.toAccountDto(account);
    }

    @GetMapping("/owner")
    public List<AccountDto> getAccountByNumber(@RequestParam Long externalId, @RequestParam OwnerType type) {
        List<Account> accounts = accountService.getAccountByOwner(externalId, type);
        return accountMapper.toAccountDtoList(accounts);
    }

    @PutMapping("/freeze")
    public AccountDto freezeAccount(@RequestParam String number) {
        Account account = accountService.freezeAccount(number);
        return accountMapper.toAccountDto(account);
    }

    @PutMapping("/unfreeze")
    public AccountDto unfreezeAccount(@RequestParam String number) {
        Account account = accountService.unfreezeAccount(number);
        return accountMapper.toAccountDto(account);
    }

    @PutMapping("/close")
    public AccountDto closeAccount(@RequestParam String number) {
        Account account = accountService.closeAccount(number);
        return accountMapper.toAccountDto(account);
    }
}
