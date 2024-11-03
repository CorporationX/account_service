package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable @Positive Long id) {
        return accountService.getAccountById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@RequestBody @Valid @NotNull AccountDto account) {
        return accountService.openAccount(account);
    }

    @PatchMapping("/{id}/froze")
    public AccountDto frozeAccount(@PathVariable @Positive Long id) {
        return accountService.frozeAccount(id);
    }

    @PatchMapping("/{id}/block")
    public AccountDto blockAccount(@PathVariable @Positive Long id) {
        return accountService.blockAccount(id);
    }

    @PatchMapping("/{id}/close")
    public AccountDto closeAccount(@PathVariable @Positive Long id) {
        return accountService.closeAccount(id);
    }
}
