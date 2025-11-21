package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/account")
@Validated
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable Long id) {
        return accountService.getById(id);
    }

    @GetMapping("/{number}")
    public AccountDto getAccountByNumber(@NotNull @PathVariable String number) {
        return accountService.getByAccountNumber(number);
    }

    @GetMapping("/{number}/balance")
    public BigDecimal getBalance(@NotNull @PathVariable String number) {
        return accountService.getBalance(number);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@RequestBody @Valid CreateAccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PutMapping("/{number}/block")
    public AccountDto blockAccount(@NotNull @PathVariable String number) {
        return accountService.blockAccount(number);
    }

    @PutMapping("/{number}/unblock")
    public AccountDto unblockAccount(@NotNull @PathVariable String number) {
        return accountService.unblockAccount(number);
    }

    @PutMapping("/{number}/deposit")
    public AccountDto deposit(@NotNull @PathVariable String number,
                              @RequestParam @Positive BigDecimal amount) {
        return accountService.deposit(number, amount);
    }

    @PutMapping("/{number}/withdraw")
    public AccountDto withdraw(@NotNull @PathVariable String number,
                               @RequestParam @Positive BigDecimal amount) {
        return accountService.withdraw(number, amount);
    }

    @PutMapping("/{number}/close")
    public AccountDto closeAccount(@NotNull @PathVariable String number) {
        return accountService.closeAccount(number);
    }
}
