package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balances")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/{accountId}")
    public ResponseEntity<BalanceDto> getBalance(@PathVariable("accountId") Long accountId) {
        return ResponseEntity.ok(balanceService.getByAccountId(accountId));
    }

    @PostMapping("/{accountId}")
    public ResponseEntity<BalanceDto> createBalance(@PathVariable("accountId") Long accountId) {
        return ResponseEntity.ok(balanceService.create(accountId));
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<BalanceDto> updateBalance(
            @PathVariable("accountId") Long accountId,
            @Valid @RequestBody BalanceDto balanceDto) {
        return ResponseEntity.ok(balanceService.updateBalanceSafely(accountId, balanceDto));
    }
}
