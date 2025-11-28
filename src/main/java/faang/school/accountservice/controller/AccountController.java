package faang.school.accountservice.controller;


import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.dto.BlockAccountRequest;
import faang.school.accountservice.dto.OpenAccountRequest;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base.path}/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;
    @Value("${api.base.path}")
    private String apiBasePath;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {
        log.info("GET {}/accounts/{}", apiBasePath, accountId);
        AccountResponse response = accountService.getAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @PathVariable String accountNumber) {
        log.info("GET {}/accounts/number/{}", apiBasePath, accountNumber);
        AccountResponse response = accountService.getAccountByNumber(accountNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<AccountResponse>> getOwnerAccounts(
            @PathVariable Long ownerId,
            @RequestParam(name = "owner_type") OwnerType ownerType) {
        log.info("GET {}/accounts/owner/{} with type: {}", apiBasePath, ownerId, ownerType);
        List<AccountResponse> responses = accountService.getOwnerAccounts(ownerId, ownerType);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/owner/{ownerId}/currency/{currency}")
    public ResponseEntity<List<AccountResponse>> getActiveAccountsByCurrency(
            @PathVariable Long ownerId,
            @RequestParam(name = "owner_type") OwnerType ownerType,
            @PathVariable Currency currency) {
        log.info("GET {}/accounts/owner/{}/currency/{}", apiBasePath, ownerId, currency);
        List<AccountResponse> responses = accountService.getActiveAccountsByCurrency(
                ownerId, ownerType, currency);
        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<AccountResponse> openAccount(
            @Valid @RequestBody OpenAccountRequest request) {
        log.info("POST {}/accounts with request: {}", apiBasePath, request);
        AccountResponse response = accountService.openAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{accountId}/block")
    public ResponseEntity<AccountResponse> blockAccount(
            @PathVariable Long accountId,
            @RequestBody(required = false) BlockAccountRequest request) {
        log.info("PATCH {}/accounts/{}/block", apiBasePath, accountId);
        String reason = request != null ? request.reason() : "No reason provided";
        AccountResponse response = accountService.blockAccount(accountId, reason);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/freeze")
    public ResponseEntity<AccountResponse> freezeAccount(@PathVariable Long accountId) {
        log.info("PATCH {}/accounts/{}/freeze", apiBasePath, accountId);
        AccountResponse response = accountService.freezeAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/unfreeze")
    public ResponseEntity<AccountResponse> unfreezeAccount(@PathVariable Long accountId) {
        log.info("PATCH {}/accounts/{}/unfreeze", apiBasePath, accountId);
        AccountResponse response = accountService.unfreezeAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{accountId}/close")
    public ResponseEntity<AccountResponse> closeAccount(@PathVariable Long accountId) {
        log.info("PATCH {}/accounts/{}/close", apiBasePath, accountId);
        AccountResponse response = accountService.closeAccount(accountId);
        return ResponseEntity.ok(response);
    }
}