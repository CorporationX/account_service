package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
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
    public AccountDto getAccount(@PathVariable String accountNumber) {
        return accountService.getAccount(accountNumber);
    }

    @PostMapping
    public AccountDto openAccount(@RequestBody AccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PutMapping("/{accountNumber}/close")
    public void closeAccount(@PathVariable String accountNumber) {
        accountService.closeAccount(accountNumber);
    }

    @PutMapping("/{accountNumber}/block")
    public void blockAccount(@PathVariable String accountNumber) {
        accountService.blockAccount(accountNumber);
    }
}
