package faang.school.accountservice.controller;

import faang.school.accountservice.model.Balance;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/account/balance")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public Balance getBalance(@PathVariable Long accountId) {
        return balanceService.getBalance(accountId);
    }

    @PutMapping("/{accountId}")
    @ResponseStatus(HttpStatus.OK)
    public Balance updateBalance(@PathVariable Long accountId,
                                 @RequestParam BigDecimal newCurrentBalance,
                                 @RequestParam BigDecimal newAuthorizedBalance) {
        return balanceService.updateBalance(accountId, newCurrentBalance, newAuthorizedBalance);
    }
}
