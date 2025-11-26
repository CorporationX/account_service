package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("api/v1")
@RestController
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping("/account/{accountId}/balance")
    public BalanceResponseDto create(
            @Positive(message = "Account cannot be negative")
            @PathVariable
            Long accountId) {
        return balanceService.createBalance(accountId);
    }

    @PutMapping("/balance/{accountId}")
    public BalanceResponseDto update(
            @Positive(message = "Account cannot be negative")
            @PathVariable
            Long accountId,
            BalanceUpdateDto balanceUpdateDto) {
        return balanceService.updateBalance(accountId, balanceUpdateDto);
    }

    @GetMapping("/balance/{balanceId}")
    public BalanceResponseDto getBalance(
            @Positive(message = "Balance cannot be negative")
            @PathVariable
            Long balanceId) {
        return balanceService.getBalance(balanceId);
    }
}