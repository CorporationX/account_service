package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getAccount(@Positive @PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

    @PostMapping("/open")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@Validated @RequestBody AccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PutMapping("/{accountId}/freeze")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto freezeAccount(@Positive @PathVariable Long accountId) {
        return accountService.freezeAccount(accountId);
    }

    @PutMapping("/{accountId}/close")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto closeAccount(@Positive @PathVariable Long accountId) {
        return accountService.closeAccount(accountId);
    }

    @PostMapping("/balance/{accountId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BalanceDto createNewAccountBalance(@PathVariable Long accountId) {
        return accountService.createBalance(accountId);
    }

    @GetMapping("/balance/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public BalanceDto getAccountBalance(@PathVariable long accountId) {
        return accountService.getBalance(accountId);
    }

    @PutMapping("/balance/increase/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public BalanceDto increaseAccountBalance(@PathVariable Long accountId,
                                             @RequestParam BigDecimal amount) {
        return accountService.increaseBalance(accountId, amount);
    }

    @PutMapping("/balance/reduce/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public BalanceDto reduceAccountBalance(@PathVariable Long accountId,
                                           @RequestParam BigDecimal amount) {
        return accountService.reduceBalance(accountId, amount);
    }
}
