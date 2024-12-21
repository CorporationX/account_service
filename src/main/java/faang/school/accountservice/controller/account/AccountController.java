package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.entity.account.Status;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Validated
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{ownerId}")
    public List<AccountDto> getAccount(@PathVariable long ownerId, @RequestParam long ownerType) {
        return accountService.getAccount(ownerId, ownerType);
    }

    @PostMapping
    public AccountDto openNewAccount(@RequestBody @Valid CreateAccountDto createAccountDto) {
        return accountService.openNewAccount(createAccountDto);
    }

    @PutMapping("/{accountId}")
    public AccountDto changeStatus(@PathVariable long accountId, @RequestParam Status status) {
        return accountService.changeStatus(accountId, status);
    }
}
