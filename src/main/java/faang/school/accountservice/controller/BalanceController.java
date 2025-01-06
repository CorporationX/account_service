package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.utilities.UrlUtils;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Validated
@RestController
@RequestMapping(UrlUtils.MAIN_URL + UrlUtils.V1 + UrlUtils.BALANCE)
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping(path = UrlUtils.ID)
    public BalanceDto getBalance(@PathVariable("id") @Min(1) Long accountId) {
        return balanceService.getBalance(accountId);
    }

    @PostMapping(path = UrlUtils.ID)
    public BalanceDto createBalance(@PathVariable("id") @Min(1) Long accountId) {
        return balanceService.createBalance(accountId);
    }

    @PutMapping(path = UrlUtils.ID + UrlUtils.AUTHORIZATION)
    public BalanceDto authorizationBalance(@PathVariable("id") @Min(1) Long accountId, @Min(0) @RequestParam() BigDecimal amount) {
        return balanceService.authorizationBalance(accountId, amount);
    }

    @PutMapping(path = UrlUtils.ID + UrlUtils.AUTHORIZATION_BALANCE)
    public BalanceDto updateAuthorizationBalance(@PathVariable("id") @Min(1) Long accountId, @Min(0) @RequestParam() BigDecimal amount) {
        return balanceService.updateAuthorizationBalance(accountId, amount);
    }

    @PutMapping(path = UrlUtils.ID + UrlUtils.ACTUAL_BALANCE)
    public BalanceDto updateActualBalance(@PathVariable("id") @Min(1) Long accountId, @Min(0) @RequestParam() BigDecimal amount) {
        return balanceService.updateActualBalance(accountId, amount);
    }
}
