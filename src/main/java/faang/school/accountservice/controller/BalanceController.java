package faang.school.accountservice.controller;

import faang.school.accountservice.model.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
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
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/{id}")
    public BalanceDto getBalance(@PathVariable("id") long balanceId) {
        return balanceService.getBalance(balanceId);
    }

    @PostMapping
    public BalanceDto createBalance(@RequestBody @Validated BalanceDto balanceDto) {
        return balanceService.createBalance(balanceDto);
    }

    @PutMapping
    public BalanceDto updateBalance(@RequestBody @Validated BalanceDto balanceDto) {
        return balanceService.updateBalance(balanceDto);
    }
}
