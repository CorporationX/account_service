package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.AmountChangeRequest;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/balance")
@RequiredArgsConstructor
@Validated
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/{balanceId}")
    public BalanceDto getBalance(@PathVariable @Positive Long balanceId) {
        return balanceService.getBalanceById(balanceId);
    }

    @GetMapping("/{accountId}/account")
    public BalanceDto getBalanceByAccountId(@PathVariable @Positive Long accountId) {
        return balanceService.getBalanceByAccountId(accountId);
    }

    @PostMapping("/{accountId}/account")
    public BalanceDto createBalance(@PathVariable @Positive Long accountId) {
        return balanceService.createBalance(accountId);
    }

    @PutMapping("/{balanceId}")
    public BalanceDto changeBalance(@PathVariable @Positive Long balanceId,
                                    @Valid @RequestBody AmountChangeRequest amount) {
        return balanceService.changeBalance(balanceId, amount);
    }
}