package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.dto.account.UpdateAccountDto;
import faang.school.accountservice.dto.filter.AccountFilterDto;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public AccountDto getAccount(@PathVariable @Positive Long accountId) {
        return accountService.getAccount(accountId);
    }

    @GetMapping
    public Page<AccountDto> getAllAccountsByOwnerId(AccountFilterDto filterDto,
                                           @RequestParam Long ownerId,
                                           @RequestParam(defaultValue = "1") @Positive int page,
                                           @RequestParam(defaultValue = "5") @Positive int size) {
        return accountService.getAllAccounts(filterDto, ownerId, page - 1, size);
    }

    @PostMapping
    public AccountDto createAccount(@RequestBody @Valid CreateAccountDto createAccountDto) {
        return accountService.createAccount(createAccountDto);
    }

    @PutMapping("/{accountId}")
    public AccountDto updateAccount(@PathVariable @Positive Long accountId,
                                    @RequestBody @Valid UpdateAccountDto updateAccountDto) {
        return accountService.updateAccount(accountId, updateAccountDto);
    }
}
