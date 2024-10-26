package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.response.BalanceResponseDto;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.entity.balance.Balance;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/balances")
@RestController
public class BalanceController {
    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    @GetMapping("/{balanceId}")
    public BalanceResponseDto findBalanceByAccountNumber(@PathVariable UUID balanceId) {
        Balance balance = balanceService.findById(balanceId);
        return balanceMapper.toBalanceResponseDto(balance);
    }
}
