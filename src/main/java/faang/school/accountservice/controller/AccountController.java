package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {
    private final AccountService accountService;
    private final FreeAccountNumbersService freeAccountNumbersService;

    @PostMapping("/accounts")
    public AccountDto create(@RequestBody CreateAccountDto createAccountDto) {
        return accountService.create(createAccountDto);
    }

    @GetMapping("/accounts/{id}")
    public AccountDto get(@PathVariable long id) {
        return null;
    }
}
