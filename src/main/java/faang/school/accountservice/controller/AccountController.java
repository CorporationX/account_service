package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.UpdateAccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping()
    public AccountDto createAccount(@RequestBody AccountDto accountDto) {
        return accountService.createAccount(accountDto);
    }

    @PutMapping("/{accountId}")
    public AccountDto updateAccount(@PathVariable long accountId, @RequestBody UpdateAccountDto updateAccountDto) {
        return accountService.updateAccount(accountId, updateAccountDto);
    }
}
