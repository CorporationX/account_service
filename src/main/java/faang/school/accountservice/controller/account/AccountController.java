package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public List<AccountDto> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable("id") long accountId) {
        return accountService.getAccountByAccountId(accountId);
    }

    @PostMapping("/open")
    public AccountDto createAccount(@RequestBody AccountCreateDto accountCreateDto) {
        return accountService.createAccount(accountCreateDto);
    }

    @PatchMapping("/{id}/block")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountDto blockAccount(@PathVariable("id") long accountId) {
        return accountService.blockAccount(accountId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAccount(@PathVariable("id") long accountId) {
        accountService.closeAccount(accountId);
    }
}
