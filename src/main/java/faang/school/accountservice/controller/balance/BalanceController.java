package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balances")
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping
    public ResponseEntity<BalanceDto> getBalance(@PathVariable Long balanceId) {
        BalanceDto balanceDto = balanceService.getBalance(balanceId);
        return ResponseEntity.ok(balanceDto);
    }
}