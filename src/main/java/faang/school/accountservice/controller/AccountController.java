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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@Validated
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto createAccount(
            @RequestBody
            @Valid
            CreateAccountDto createAccountDto
            ) {
        return accountService.createAccount(createAccountDto);
    }

    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getAccount(
            @PathVariable
            @NotNull(message = "accountId is required")
            @Positive(message = "accountId must be positive")
            Long accountId
    ) {
        return accountService.getByAccountId(accountId);
    }

    @PostMapping("/{accountId}/block")
    @ResponseStatus(HttpStatus.OK)
    public void blockAccount(
            @PathVariable
            @NotNull(message = "accountId is required")
            @Positive(message = "accountId must be positive")
            Long accountId
    ) {
        accountService.blockAccount(accountId);
    }

    @PostMapping("/{accountId}/close")
    @ResponseStatus(HttpStatus.OK)
    public void closeAccount(
            @PathVariable
            @NotNull(message = "accountId is required")
            @Positive(message = "accountId must be positive")
            Long accountId
    ) {
        accountService.closeAccount(accountId);
    }
}