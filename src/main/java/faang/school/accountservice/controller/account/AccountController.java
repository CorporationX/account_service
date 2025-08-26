package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.AccountDto;
import faang.school.accountservice.dto.account.CreateAccountDto;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.service.account.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account", description = "Account operations")
@Validated
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}/balance")
    public ResponseEntity<Long> getAccountBalance(@PathVariable Long id) {
        return ResponseEntity.ok(1000L);
    }

    @Operation(
            summary = "Create new account",
            description = "Creates a new account in specified status, or ACTIVE status by default"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public AccountDto create(@RequestBody @Valid CreateAccountDto createAccountDto) {
        return accountService.create(createAccountDto);
    }

    @Operation(summary = "Get account by id")
    @GetMapping("/{id}")
    public AccountDto get(@PathVariable Long id) {
        return accountService.getAccountById(id);
    }

    @Operation(summary = "Block account by id")
    @PostMapping("/{id}/block")
    public AccountDto block(@PathVariable Long id) {
        return accountService.updateAccountStatus(id, AccountStatus.FROZEN);
    }

    @Operation(summary = "Close account by id")
    @PostMapping("/{id}/close")
    public AccountDto close(@PathVariable Long id) {
        return accountService.updateAccountStatus(id, AccountStatus.CLOSED);
    }
}
