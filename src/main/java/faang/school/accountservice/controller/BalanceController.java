package faang.school.accountservice.controller;

import faang.school.accountservice.config.annotation.ValidAccountNumber;
import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;


@Slf4j
@RestController
@RequestMapping("/accounts/balances")
@RequiredArgsConstructor
@Validated
@Tag(name = "Balance API", description = "Endpoints for operations with balances")
public class BalanceController {

    private final BalanceService balanceService;

    @PostMapping()
    @Operation(summary = "Authorize balance")
    public ResponseEntity<BalanceDto> authorizeBalance(@ValidAccountNumber
                                                       @RequestParam
                                                       String accountNumber) {
        log.info("Request to authorize balance for account number: {}", accountNumber);
        return ResponseEntity.status(HttpStatus.CREATED).body(balanceService.authorize(accountNumber));
    }

    @PutMapping("/authorized/deposit")
    @Operation(summary = "Deposit authorized balance")
    public ResponseEntity<BalanceDto> depositAuthorized(@ValidAccountNumber
                                                        @RequestParam
                                                        String accountNumber,
                                                        @Positive(message = "Amount must be positive value")
                                                        @RequestParam
                                                        BigDecimal amount) {
        log.info("Request to deposit authorized balance. Account Number: {}, Amount: {}", accountNumber, amount);
        return ResponseEntity.ok(balanceService.depositAuthorized(accountNumber, amount));
    }

    @PutMapping("/authorized/withdraw")
    @Operation(summary = "Withdraw authorized balance")
    public ResponseEntity<BalanceDto> withdrawAuthorized(@ValidAccountNumber
                                                         @RequestParam
                                                         String accountNumber,
                                                         @Positive(message = "Amount must be positive value")
                                                         @RequestParam
                                                         BigDecimal amount) {
        log.info("Request to withdraw authorized balance. Account Number: {}, Amount: {}", accountNumber, amount);
        return ResponseEntity.ok(balanceService.withdrawAuthorized(accountNumber, amount));
    }

    @PutMapping("/actual/deposit")
    @Operation(summary = "Deposit actual balance")
    public ResponseEntity<BalanceDto> depositActual(@ValidAccountNumber
                                                    @RequestParam
                                                    String accountNumber,
                                                    @Positive(message = "Amount must be positive value")
                                                    @RequestParam
                                                    BigDecimal amount) {
        log.info("Request to deposit actual balance. Account Number: {}, Amount: {}", accountNumber, amount);
        return ResponseEntity.ok(balanceService.depositActual(accountNumber, amount));
    }

    @PutMapping("/actual/withdraw")
    @Operation(summary = "Withdraw actual balance")
    public ResponseEntity<BalanceDto> withdrawActual(@ValidAccountNumber
                                                     @RequestParam
                                                     String accountNumber,
                                                     @Positive(message = "Amount must be positive value")
                                                     @RequestParam
                                                     BigDecimal amount) {
        log.info("Request to withdraw actual balance. Account Number: {}, Amount: {}", accountNumber, amount);
        return ResponseEntity.ok(balanceService.withdrawActual(accountNumber, amount));
    }
}
