package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.service.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
@Tag(name = "Account controller", description = "Controller with accounts REST endpoints")
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Get all accounts"
    )
    @GetMapping
    public List<AccountDto> getAccounts() {
        return accountService.getAccounts();
    }

    @Operation(
            summary = "Get account",
            description = "Get account by ID"
    )
    @GetMapping("/{id}")
    public AccountDto getAccount(@PathVariable("id") @Parameter(description = "account Id") long accountId) {
        return accountService.getAccountByAccountId(accountId);
    }

    @Operation(
            summary = "Open account",
            description = "Generate new account"
    )
    @PostMapping("/open")
    public AccountDto createAccount(@RequestBody @Valid AccountCreateDto accountCreateDto) {
        return accountService.createAccount(accountCreateDto);
    }

    @Operation(
            summary = "Block account",
            description = "Set account status FREEZE by account id"
    )
    @PatchMapping("/{id}/block")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AccountDto blockAccount(@PathVariable("id") @Parameter(description = "account Id") long accountId) {
        return accountService.blockAccount(accountId);
    }

    @Operation(
            summary = "Delete account",
            description = "Set account status CLOSED by account id"
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void closeAccount(@PathVariable("id") @Parameter(description = "account Id") long accountId) {
        accountService.closeAccount(accountId);
    }

}
