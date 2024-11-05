package faang.school.accountservice.controller;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAuthPayment;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/balances")
public class BalanceController {
    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    @GetMapping
    public BalanceDto getBalance(@RequestParam @NotNull UUID accountUuid) {
        Balance balance = balanceService.getBalance(accountUuid);
        return balanceMapper.toDto(balance);
    }

    @PostMapping("/{balanceId}")
    public void createBalance(@Valid @PathVariable UUID balanceId, @Valid @RequestBody Money money) {
        BalanceAuthPayment balanceAuthPayment = balanceService.createAuthPayment(balanceId, money);
    }

    @PutMapping("/{balanceAuthId}")
    public void rejectAuthPayment(@Valid @PathVariable UUID balanceAuthId) {
        BalanceAuthPayment balanceAuthPayment = balanceService.rejectAuthPayment(balanceAuthId);
    }
}
