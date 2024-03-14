package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    @GetMapping
    public BalanceDto getBalance(@RequestParam String accountNumber) {
        return balanceMapper.toDto(balanceService.getBalance(accountNumber));
    }

    @PostMapping
    public BalanceDto deposit(@RequestParam String accountNumber, @RequestParam BigDecimal amount) {
        return balanceService.deposit(accountNumber, amount);
    }

}
