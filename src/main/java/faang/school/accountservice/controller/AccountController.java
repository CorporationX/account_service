package faang.school.accountservice.controller;

import faang.school.accountservice.annotation.AccountNumberConstraint;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${domain.path}/accounts")
@Validated
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public AccountDto getAccountById(@PathVariable @Min(1) long id) {
        return accountService.getAccountById(id);
    }

    @GetMapping("/owner")
    public List<AccountDto> getOwnerAccounts(@RequestParam @Min(1) long ownerId, @RequestParam String ownerType) {
        return accountService.getOwnerAccounts(ownerId, ownerType);
    }

    @GetMapping("/number/{number}")
    public AccountDto getAccountByNumber(@PathVariable @AccountNumberConstraint String number) {
        return accountService.getAccountByNumber(number);
    }

    @PostMapping
    public AccountDto createAccount(@RequestBody @Valid AccountDto dto) {
        return accountService.createAccount(dto);
    }

    @PutMapping("/block/{id}")
    public void blockAccount(@PathVariable @Min(1) long id) {
        accountService.blockAccount(id);
    }

    @PutMapping("/close/{id}")
    public void closeAccount(@PathVariable @Min(1) long id) {
        accountService.closeAccount(id);
    }
}