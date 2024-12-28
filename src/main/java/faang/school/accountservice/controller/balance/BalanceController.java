package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/account/{accountId}/balance/")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;


    @GetMapping()
    public ResponseBalanceDto getBalanceByAccountId(@PathVariable @NotNull Long accountId) {
        return balanceService.getBalance(accountId);
    }
}
