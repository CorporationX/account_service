package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountCreateDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountReasonDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
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

@RequiredArgsConstructor
@RequestMapping("/account")
@RestController
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public AccountDto getMyAccount(@PathVariable Long accountId) {
        return accountService.getMyAccount(accountId);
    }

    @PostMapping("/open")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountDto openAccount(@Valid @RequestBody AccountCreateDto dto) {
        return accountService.openAccount(dto);
    }

    @PatchMapping("/{accountId}/block")
    public AccountDto blockAccount(@PathVariable Long accountId, @Valid @RequestBody AccountReasonDto dto) {
        return accountService.blockAccount(accountId, dto);
    }

    @PatchMapping("/{accountId}/freeze")
    public AccountDto freezeAccount(@PathVariable Long accountId, @Valid @RequestBody AccountReasonDto dto) {
        return accountService.freezeAccount(accountId, dto);
    }

    @PatchMapping("/{accountId}/close")
    public AccountDto closeAccount(@PathVariable Long accountId, @Valid @RequestBody AccountReasonDto dto) {
        return accountService.closeAccount(accountId, dto);
    }
}
