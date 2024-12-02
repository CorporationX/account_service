package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceRequest;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts/{accountId}/balance")
//@Validated
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @PutMapping
    public BalanceDto update(@PathVariable @Positive long accountId,
                             @RequestBody  UpdateBalanceRequest updateBalanceRequest) {
        return balanceService.update(accountId, updateBalanceRequest);
    }
}
