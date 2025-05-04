package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.BalanceViewDto;
import faang.school.accountservice.service.balance.BalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@Tag(name = "Balance Operations",
        description = "API for managing account balances and authorizations")
@RequestMapping("balance/{accountId}")
public class BalanceController {
    private final BalanceService balanceService;

    @Operation(
            summary = "Authorize funds",
            description = "Reserves the specified amount from available balance " +
                    "(holds funds for future transaction)")
    @PostMapping("/authorize")
    public BalanceViewDto authorizeAmount(@NotNull @PathVariable Long accountId,
                                          @NotNull @RequestParam @Positive BigDecimal amount) {
        return balanceService.authorizeAmount(accountId, amount);
    }

    @Operation(
            summary = "Clear authorized funds",
            description = "Completes the transaction by transferring authorized funds to recipient")
    @PostMapping("/clear")
    public BalanceViewDto clearAuthorizedAmount(@NotNull @PathVariable Long accountId,
                                                @NotNull @RequestParam @Positive BigDecimal amount) {
        return balanceService.clearAuthorizedAmount(accountId, amount);
    }

    @Operation(
            summary = "Deposit funds",
            description = "Adds specified amount to account's available balance")
    @PostMapping("/deposit")
    public BalanceViewDto depositAmount(@NotNull @PathVariable Long accountId,
                                        @NotNull @RequestParam @Positive BigDecimal amount) {
        return balanceService.depositAmount(accountId, amount);
    }

    @Operation(
            summary = "Cancel authorization",
            description = "Releases previously authorized funds back to available balance")
    @PostMapping("/cancel-auth")
    public BalanceViewDto cancelAuthorization(@NotNull @PathVariable Long accountId,
                                              @NotNull @RequestParam @Positive BigDecimal amount) {
        return balanceService.cancelAuthorization(accountId, amount);
    }

    @Operation(
            summary = "Get available balance",
            description = "Returns current available balance (actual balance)")
    @GetMapping("/available-balance")
    public BigDecimal getAvailableBalance(@NotNull @PathVariable Long accountId) {
        return balanceService.getAvailableBalance(accountId);
    }
}