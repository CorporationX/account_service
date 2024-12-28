package faang.school.accountservice.controller;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.BalanceChangeDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.dto.TransactionRequestDto;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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

    @PostMapping("/{accountNumber}/deposit")
    @Operation(summary = "Deposit funds on the account")
    public ResponseEntity<BalanceChangeDto> deposit(
            @PathVariable @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters") String accountNumber,
            @Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        log.info("Request to deposit funds on the account: number: {}, amount: {}", transactionRequestDto.accountNumber(), transactionRequestDto.amount());

        return ResponseEntity.ok(accountService.deposit(transactionRequestDto));
    }

    @PostMapping("/{accountNumber}/withdraw")
    @Operation(summary = "Withdraw funds from the account")
    public ResponseEntity<BalanceChangeDto> withdraw(
            @PathVariable @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters") String accountNumber,
            @Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        Long ownerId = userContext.getUserId();
        log.info("Request to withdraw funds from the account: number: {}, amount: {}", transactionRequestDto.accountNumber(), transactionRequestDto.amount());

        return ResponseEntity.ok(accountService.withdraw(ownerId, transactionRequestDto));
    }

    @PostMapping("/{accountNumber}/transactions/{transactionId}/approve")
    @Operation(summary = "Approve the pending transaction", description = "Endpoint to be used by payment systems only!")
    public ResponseEntity<Void> approve(
            @PathVariable @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters") String accountNumber,
            @PathVariable @Positive(message = "Transaction id should be positive") Long transactionId,
            @Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        log.info("Request to approve the transaction: number: {}, amount: {}", transactionId, transactionRequestDto.amount());
        accountService.approve(transactionRequestDto, transactionId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountNumber}/transactions/{transactionId}/reject")
    @Operation(summary = "Reject the pending transaction")
    public ResponseEntity<Void> reject(
            @PathVariable @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters") String accountNumber,
            @PathVariable @Positive(message = "Transaction id should be positive") Long transactionId,
            @Valid @RequestBody TransactionRequestDto transactionRequestDto) {
        log.info("Request to cancel the transaction: number: {}, amount: {}", transactionId, transactionRequestDto.amount());
        accountService.reject(transactionRequestDto, transactionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get the balance information")
    public ResponseEntity<AccountBalanceDto> getAccountBalance(
            @PathVariable @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters") String accountNumber
    ) {
        Long ownerId = userContext.getUserId();
        log.info("Request to get account balance information: account number: {}", accountNumber);
        return ResponseEntity.ok().body(accountService.getAccountBalance(ownerId, accountNumber));
    }

    @GetMapping("/{accountNumber}/transactions")
    @Operation(summary = "Get all transactions list")
    public ResponseEntity<List<TransactionDto>> getTransactions(
            @PathVariable @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters") String accountNumber
    ) {
        Long ownerId = userContext.getUserId();
        log.info("Request to get transactions list: account number: {}", accountNumber);
        return ResponseEntity.ok(accountService.getTransactions(ownerId, accountNumber));
    }
}
