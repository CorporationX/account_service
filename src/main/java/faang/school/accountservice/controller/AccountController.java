package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${account-service.api-version}/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping("/create")
    public void createAccount(@RequestBody @Valid AccountDto dto) {
        accountService.createAccount(dto);
    }

    @GetMapping("/{accountId}")
    public AccountDto getAccount(@PathVariable long accountId) {
        return accountService.findAccountById(accountId);
    }

    @DeleteMapping("/delete/{accountId}")
    public void deleteAccount(@PathVariable long accountId) {
        accountService.deleteAccountById(accountId);
    }

    @PostMapping
    public AccountDto updateAccount(@RequestBody AccountDto dto) {
        return accountService.updateAccount(dto);
    }
}
