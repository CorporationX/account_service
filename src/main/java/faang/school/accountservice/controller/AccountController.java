package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.ChangeAccountStatusReasonDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public AccountDto getMyAccount(@PathVariable UUID accountId) {
        return accountService.getMyAccount(accountId);
    }

    @PostMapping("/open")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@Valid @RequestBody AccountCreateDto dto) {
        return accountService.openAccount(dto);
    }

    @PostMapping("/{accountId}/block")
    public AccountDto blockAccount(@PathVariable UUID accountId, @Valid @RequestBody ChangeAccountStatusReasonDto dto) {
        return accountService.blockAccount(accountId, dto);
    }

    @PostMapping("/{accountId}/freeze")
    public AccountDto freezeAccount(@PathVariable UUID accountId, @Valid @RequestBody ChangeAccountStatusReasonDto dto) {
        return accountService.freezeAccount(accountId, dto);
    }

    @PostMapping("/{accountId}/close")
    public AccountDto closeAccount(@PathVariable UUID accountId, @Valid @RequestBody ChangeAccountStatusReasonDto dto) {
        return accountService.closeAccount(accountId, dto);
    }
}
