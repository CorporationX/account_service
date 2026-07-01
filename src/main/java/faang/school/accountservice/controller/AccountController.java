package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountFilterDto;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.validator.AccountValidator;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Slf4j
public class AccountController {
    private final AccountService accountService;
    private final AccountValidator accountValidator;

    @PostMapping()
    @Operation(
            summary = "Create account",
            description = "Creates a new account of the type specified in the request body"
    )
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        accountValidator.validateAccountOwner(accountDto);

        AccountDto createdAccount = accountService.createAccount(accountDto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAccount.getId())
                .toUri();

        return ResponseEntity.created(location).body(createdAccount);
    }

    @PutMapping("/{id}/block")
    @Operation(
            summary = "Block account",
            description = "Blocks the account with the specified ID, making it unavailable for further operations"
    )
    public ResponseEntity<AccountDto> blockAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.blockAccount(id));
    }

    @PutMapping("/{id}/close")
    @Operation(
            summary = "Close account",
            description = "Closes the account with the specified ID"
    )
    public ResponseEntity<AccountDto> closeAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.closeAccount(id));
    }

    @PostMapping("/filter")
    @Operation(
            summary = "Get accounts with filters",
            description = "Returns a list of accounts matching the filter criteria provided in the request body"
    )
    public ResponseEntity<List<AccountDto>> getAccountsWithFilters(@RequestBody AccountFilterDto accountFilterDto) {
        return ResponseEntity.ok(accountService.getAccountsWithFilters(accountFilterDto));
    }
}