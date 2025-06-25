package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.UpdateAccountDto;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/{accountNumber}")
    public AccountDto get(@PathVariable String accountNumber) {
        return accountService.get(accountNumber);
    }

    @PostMapping()
    public AccountDto open(@RequestBody AccountDto accountDto) {
        return accountService.open(accountDto);
    }

    @PutMapping("/close/{accountNumber}")
    public void close(@PathVariable String accountNumber) {
        accountService.close(accountNumber);
    }

    @PutMapping("/block/{accountNumber}")
    public void block(@PathVariable String accountNumber) {
        accountService.block(accountNumber);
    }

    @PutMapping("/update/{accountId}")
    public AccountDto updateAccount(@PathVariable long accountId, @RequestBody UpdateAccountDto updateAccountDto) {
        return accountService.update(accountId, updateAccountDto);
    }
}
