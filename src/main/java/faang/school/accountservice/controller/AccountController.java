package faang.school.accountservice.controller;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/accounts")
@Tag(name = "Account API", description = "Endpoints for operations with accounts")
public class AccountController {
    private final UserContext userContext;
    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Open new account")
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody CreateAccountDto dto) {
        Long ownerId = userContext.getUserId();
        log.info("Request to open new account from {} id: {}", dto.ownerType(), ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(dto, ownerId));
    }

    @GetMapping
    @Operation(summary = "Get accounts information by owner type and owner id")
    public ResponseEntity<List<AccountDto>> getAccounts(
            @Parameter(description = "Type of account owner. Valid values: USER, PROJECT", example = "USER")
            @RequestParam @NotBlank(message = "Owner type cannot be empty") String accountOwnerType) {
        AccountOwnerType ownerType = AccountOwnerType.toValue(accountOwnerType);
        Long ownerId = userContext.getUserId();
        log.info("Request to get accounts of {} id: {}", ownerType, ownerId);
        return ResponseEntity.ok(accountService.getAccounts(ownerType, ownerId));
    }

    @PutMapping("/{accountNumber}")
    @Operation(summary = "Update account status")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters") String accountNumber,
            @Parameter(description = "Account status. Valid values: ACTIVE, INACTIVE, FROZEN", example = "ACTIVE")
            @RequestParam @NotBlank(message = "New status cannot be empty") String status) {
        AccountStatus accountStatus = AccountStatus.toValue(status);
        Long ownerId = userContext.getUserId();
        log.info("Request to update account status from {} id: {}", accountNumber, ownerId);
        return ResponseEntity.ok(accountService.updateAccountStatus(accountNumber, ownerId, accountStatus));
    }

    @DeleteMapping("/{accountNumber}")
    @Operation(summary = "Delete account by its number")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters") String accountNumber) {
        Long ownerId = userContext.getUserId();
        log.info("Request to delete account from {} id: {}", accountNumber, ownerId);
        accountService.deleteAccount(accountNumber, ownerId);
        return ResponseEntity.noContent().build();
    }
}
