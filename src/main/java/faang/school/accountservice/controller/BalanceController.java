package faang.school.accountservice.controller;

import faang.school.accountservice.dto.Balance.BalanceDtoWhenCreate;
import faang.school.accountservice.dto.Balance.BalanceDtoWhenUpdate;
import faang.school.accountservice.dto.Balance.ReturnedBalanceDto;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts/{accountId}/balance")
@Validated
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @PostMapping
    public ReturnedBalanceDto create(@PathVariable @Positive long accountId,
                                     @RequestBody @Valid BalanceDtoWhenCreate balanceDtoWhenCreate) {
        return balanceService.create(accountId, balanceDtoWhenCreate);
    }

    @PutMapping
    public ReturnedBalanceDto update(@PathVariable @Positive long accountId,
                                     @RequestBody @Valid BalanceDtoWhenUpdate balanceDtoWhenUpdate) {
        return balanceService.update(accountId, balanceDtoWhenUpdate);
    }
}
