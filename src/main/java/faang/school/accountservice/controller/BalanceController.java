package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/balances")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/{accountId}")
    public ResponseEntity<BalanceDto> getBalanceByAccountId(@PathVariable Long accountId) {
        BalanceDto balance = balanceService.getBalanceByAccountId(accountId);
        return ResponseEntity.ok(balance);
    }

    @PostMapping
    public ResponseEntity<BalanceDto> createBalance(@RequestBody BalanceDto balanceDto) {
        BalanceDto createdBalance = balanceService.createBalance(balanceDto);
        return ResponseEntity.ok(createdBalance);
    }

    @PutMapping("/{accountId}")
    public ResponseEntity<BalanceDto> updateBalance(@PathVariable Long accountId, @RequestBody BalanceDto balanceDto) {
        BalanceDto updatedBalance = balanceService.updateBalance(accountId, balanceDto);
        return ResponseEntity.ok(updatedBalance);
    }
}