package faang.school.accountservice.controller;

import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/balances")
public class BalanceController {
    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    @GetMapping
    public BalanceDto getBalance(@RequestParam @NotBlank Long accountId) {
        Balance balance = balanceService.getBalance(accountId);
        return balanceMapper.toDto(balance);
    }

    @PutMapping
    public BalanceDto updateBalance(@RequestBody BalanceDto balanceDto) {
        Balance balance = balanceMapper.toEntity(balanceDto);
        Balance savedBalance = balanceService.updateBalance(balance);
        return balanceMapper.toDto(savedBalance);
    }
}
