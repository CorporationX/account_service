package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.UpdateAccountDto;
import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/accounts")
public class AccountsController {
    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public AccountDto getAccount(@PathVariable @Positive Long accountId) {
        return accountService.getAccount(accountId);
    }

    @GetMapping
    public List<AccountDto> getAllAccounts(@RequestBody AccountFilterDto filterDto) {
        return accountService.getAllAccounts(filterDto);
    }

    @PostMapping()
    public void createAccount(@RequestBody @Valid CreateAccountDto createAccountDto) {
        accountService.createAccount(createAccountDto);
    }

    @PutMapping()
    public void updateAccount(@RequestBody @Valid UpdateAccountDto updateAccountDto) {
        accountService.updateAccount(updateAccountDto);
    }
}
