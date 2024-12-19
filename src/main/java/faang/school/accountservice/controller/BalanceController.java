package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.TransactionDto;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/{accountId}")
    public BalanceDto getBalance(@PathVariable Long accountId) {
        return balanceService.getBalance(accountId);
    }

    @PutMapping("/{accountId}")
    public BalanceDto updateBalance(@PathVariable Long accountId,
                                 @NotNull @RequestBody TransactionDto amount) {
        return balanceService.updateBalance(accountId, amount);
    }
}
