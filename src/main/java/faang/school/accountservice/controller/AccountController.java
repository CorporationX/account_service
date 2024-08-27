package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.UpdateAccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@RequestBody @Valid AccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PutMapping("/update/{accountId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountDto updateAccount(@PathVariable long accountId, @RequestBody @Valid UpdateAccountDto updateAccountDto) {
        return accountService.updateAccount(accountId, updateAccountDto);
    }

    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.FOUND)
    public AccountDto getAccount(@PathVariable long accountId) {
        return accountService.getAccount(accountId);
    }

    @PutMapping("block/{accountId}")
    public void blockAccount(@PathVariable long accountId) {
        accountService.blockAccount(accountId);
    }

    @PutMapping("unblock/{accountId}")
    public void unblockAccount(@PathVariable long accountId) {
        accountService.unblockAccount(accountId);
    }

    @PutMapping("close/{accountId}")
    public void closeAccount(@PathVariable long accountId) {
        accountService.closeAccount(accountId);
    }

}
