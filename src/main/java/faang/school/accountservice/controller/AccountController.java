package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/account/{ownerType}/ownerType/{ownerId}/ownerId")
    public List<AccountDto> getAccountByOwner(@PathVariable OwnerType ownerType, @PathVariable Long ownerId) {
        return accountService.getAccountByOwner(ownerType, ownerId);
    }

    @GetMapping("/account/{accountId}")
    public AccountDto getAccountById(@PathVariable Long accountId) {
        return accountService.getAccountById(accountId);
    }

    @PostMapping("/account/{ownerType}/ownerType/{ownerId}/ownerId")
    public AccountDto openAccount(@RequestBody CreateAccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PutMapping("/account/block/{accountId}")
    public void blockAccount(@PathVariable Long accountId) {
        accountService.blockAccount(accountId);
    }

    @PutMapping("/account/close/{accountId}")
    public void closeAccount(@PathVariable Long accountId) {
        accountService.closeAccount(accountId);
    }
}
