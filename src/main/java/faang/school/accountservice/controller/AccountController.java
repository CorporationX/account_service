package faang.school.accountservice.controller;

import faang.school.accountservice.dto.account.AccountCreateDto;
import faang.school.accountservice.dto.account.AccountViewDto;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Account API", description = "API for managing accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieve account details by account ID")
    public ResponseEntity<AccountViewDto> getAccount(@PathVariable @Positive @NotNull Long id) {
        log.info("Fetching account with ID: {}", id);
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @GetMapping
    @Operation(summary = "Get accounts by owner", description = "Retrieve all accounts for a specific owner")
    public ResponseEntity<List<AccountViewDto>> getAccountsByOwner(
            @RequestParam OwnerType ownerType,
            @RequestParam @Positive @NotNull Long ownerId) {
        log.info("Fetching accounts for owner type: {} and owner ID: {}", ownerType, ownerId);
        return ResponseEntity.ok(accountService.getAccountsByOwner(ownerType, ownerId));
    }

    @PostMapping
    @Operation(summary = "Open a new account", description = "Create a new account with the provided details")
    public ResponseEntity<AccountViewDto> openAccount(@RequestBody @Valid AccountCreateDto createDto) {
        log.info("Opening a new account with details: {}", createDto);
        return ResponseEntity.ok(accountService.openAccount(createDto));
    }

    @PostMapping("/{id}/block")
    @Operation(summary = "Block an account", description = "Block an existing account by ID")
    public ResponseEntity<AccountViewDto> blockAccount(@PathVariable @Positive @NotNull Long id) {
        log.info("Blocking account with ID: {}", id);
        return ResponseEntity.ok(accountService.blockAccount(id));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Close an account", description = "Close an existing account by ID")
    public ResponseEntity<AccountViewDto> closeAccount(@PathVariable @Positive @NotNull Long id) {
        log.info("Closing account with ID: {}", id);
        return ResponseEntity.ok(accountService.closeAccount(id));
    }

    @PostMapping("/{accountId}/unblock")
    public ResponseEntity<AccountViewDto> unblockAccount(@PathVariable Long accountId) {
        log.info("Unblocking account with ID: {}", accountId);
        return ResponseEntity.ok(accountService.unblockAccount(accountId));
    }
}