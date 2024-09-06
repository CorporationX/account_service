package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public AccountDto openAccount(@RequestBody @Valid AccountDto accountDto) {
        return accountService.openAccount(accountDto);
    }

    @PutMapping("/freeze/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto blockAccount(@PathVariable UUID id) {
        return accountService.freezeAccount(id);
    }

    @PutMapping("/close/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto closeAccount(@PathVariable UUID id) {
        return accountService.closeAccount(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<AccountDto> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AccountDto getAccountById(@PathVariable UUID id) {
        return accountService.getAccountById(id);
    }

}
