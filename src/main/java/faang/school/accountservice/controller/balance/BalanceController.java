package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.Money;
import faang.school.accountservice.dto.balance.response.BalanceResponseDto;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/balances")
@RestController
public class BalanceController {
    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    @GetMapping("/{accountId}")
    public BalanceResponseDto findBalanceByAccountId(@PathVariable UUID accountId) {
        Balance balance = balanceService.findByAccountId(accountId);
        return balanceMapper.toBalanceResponseDto(balance);
    }

    @PutMapping("/{balanceId}")
    public BalanceResponseDto topUpCurrentBalance(@PathVariable UUID balanceId, @RequestBody Money money) {
        Balance updatedBalance = balanceService.topUpCurrentBalance(balanceId, money);
        return balanceMapper.toBalanceResponseDto(updatedBalance);
    }
}
