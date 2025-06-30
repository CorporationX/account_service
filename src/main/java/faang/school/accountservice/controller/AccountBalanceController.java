package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountBalanceDto;
import faang.school.accountservice.service.AccountBalanceService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/balances")
public class AccountBalanceController {
    private final AccountBalanceService accountBalanceService;

    @GetMapping("/{accountNumber}")
    public AccountBalanceDto get(@NotNull @NotBlank @PathVariable Long accountId) {
        log.info("Starting method get from BalanceController, for account with ID: {}", accountId);
        return accountBalanceService.get(accountId);
    }
}
