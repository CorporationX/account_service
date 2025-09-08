package faang.school.accountservice.controller;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<Balance> getBalance(@PathVariable Long accountId) {
        Balance balance = balanceService.getBalanceByAccountId(accountId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/{accountId}/authorize")
    public ResponseEntity<Balance> authorizeAmount(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount) {
        Balance balance = balanceService.authorizeAmount(accountId, amount);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/{accountId}/capture")
    public ResponseEntity<Balance> captureAmount(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount) {
        Balance balance = balanceService.captureAmount(accountId, amount);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/{accountId}/reverse")
    public ResponseEntity<Balance> reverseAuthorization(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount) {
        Balance balance = balanceService.reverseAuthorization(accountId, amount);
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/{accountId}/add-funds")
    public ResponseEntity<Balance> addFunds(
            @PathVariable Long accountId,
            @RequestParam BigDecimal amount) {
        Balance balance = balanceService.addFunds(accountId, amount);
        return ResponseEntity.ok(balance);
    }
}
