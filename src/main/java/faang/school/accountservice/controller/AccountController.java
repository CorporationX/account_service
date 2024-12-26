package faang.school.accountservice.controller;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.CreateAccountDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.enums.AccountOwnerType;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestParam @NotBlank(message = "New status cannot be empty") String status) {
        AccountStatus accountStatus = AccountStatus.toValue(status);
        Long ownerId = userContext.getUserId();
        log.info("Request to update account status from {} id: {}", accountNumber, ownerId);

        return ResponseEntity.ok(accountService.updateAccountStatus(accountNumber, ownerId, accountStatus));
    }

    @PostMapping("/deposit")
    @Operation(summary = "Deposit funds on the account")
    public ResponseEntity<AccountBalanceDto> deposit(@Valid @RequestBody TransactionDto transactionDto) {
        log.info("Request to deposit funds on the account: number: {}, amount: {}", transactionDto.accountNumber(), transactionDto.amount());

        return ResponseEntity.ok(accountService.deposit(transactionDto));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw funds from the account")
    public ResponseEntity<AccountBalanceDto> withdraw(@Valid @RequestBody TransactionDto transactionDto) {
        Long ownerId = userContext.getUserId();
        log.info("Request to withdraw funds from the account: number: {}, amount: {}", transactionDto.accountNumber(), transactionDto.amount());

        return ResponseEntity.ok(accountService.withdraw(ownerId, transactionDto));
    }

    @PostMapping("/approve")
    @Operation(summary = "Approve the pending transaction")
    public ResponseEntity<Void> approve(@Valid @RequestBody TransactionDto transactionDto) {
        log.info("Request to approve the transaction: number: {}, amount: {}", transactionDto.accountNumber(), transactionDto.amount());
        accountService.approve(transactionDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    @Operation(summary = "Cancel the pending transaction")
    public ResponseEntity<Void> cancel(@Valid @RequestBody TransactionDto transactionDto) {
        log.info("Request to cancel the transaction: number: {}, amount: {}", transactionDto.accountNumber(), transactionDto.amount());
        accountService.cancel(transactionDto);
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
}
