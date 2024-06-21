package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("accounts/")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public AccountDto open(@Valid @RequestBody AccountDto accountDto) {
        accountDto.validateCreationStatus();
        accountDto.validateAccountOwners();

        return accountService.open(accountDto);
    }

    @GetMapping("{accountId}")
    public AccountDto get(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }

    @PutMapping("{accountId}")
    public AccountDto changeStatus(@PathVariable Long accountId, @RequestParam AccountStatus status) {
        return accountService.changeStatus(accountId, status);
    }
}
