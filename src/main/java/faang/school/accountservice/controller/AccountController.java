package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/account")
@Validated
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public AccountDto getAccount(@NotNull @PathVariable Long id) {
        return accountService.getById(id);
    }

    @GetMapping("/{number}")
    public AccountDto getAccountByNumber(@NotNull @PathVariable String number) {
        return accountService.getByAccountNumber(number);
    }

    @GetMapping("/{number}/balance")
    public Double getBalance(@NotNull @PathVariable String number) {
        return accountService.getBalance(number);
    }
}
