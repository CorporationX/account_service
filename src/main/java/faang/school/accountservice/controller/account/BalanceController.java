package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.BalanceDto;
import faang.school.accountservice.service.account.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/{accountId}/balances")
    public BalanceDto getBalanceByAccount(@PathVariable Long accountId) {
        return balanceService.getBalanceByAccount(accountId);
    }

    @PostMapping("/{accountId}/balances")
    public BalanceDto createBalanceForAccount(@PathVariable Long accountId) {
        return balanceService.createBalanceForAccount(accountId);
    }

    @PatchMapping("/balances")
    public BalanceDto updateBalanceForAccount(@RequestBody @Valid BalanceDto balanceDto) {
        return balanceService.updateBalanceForAccount(balanceDto);
    }
}
