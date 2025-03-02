package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
@RequiredArgsConstructor
@RequestMapping("${base-url}/balance")
@Validated
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/{accountId}")
    public BalanceDto getBalanceByAccountId(@PathVariable @Min(1) long accountId) {
        return balanceService.getBalanceByAccountId(accountId);
    }

    @PostMapping
    public BalanceDto createBalance(@RequestBody @Valid BalanceDto dto) {
        return balanceService.createBalance(dto);
    }

    @PutMapping
    public BalanceDto updateBalance(@RequestBody @Valid BalanceDto dto) {
        return balanceService.updateBalance(dto);
    }
}
