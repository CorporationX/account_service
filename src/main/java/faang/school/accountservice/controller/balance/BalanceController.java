package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.TransactionDto;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account/{accountId}/balance/")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping
    public BalanceDto getBalance(@PathVariable @Positive long accountId) {
        return balanceService.getBalance(accountId);
    }

    @PutMapping
    public BalanceDto update(@PathVariable @Positive long accountId,
                             @RequestBody @Valid TransactionDto transactionDto) {
        return balanceService.update(transactionDto);
    }
}
