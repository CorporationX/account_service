package faang.school.accountservice.controller;

import faang.school.accountservice.model.AccountBalance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.service.AccountBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountServiceController {

    private final AccountBalanceService accountBalanceService;

    @GetMapping("/{accountId}")
    public AccountBalance getBalance(@PathVariable Long accountId) {
        return accountBalanceService.getBalance(accountId);
    }

    @GetMapping("/{accountId}/audit")
    public List<BalanceAudit> getAudit(@PathVariable Long accountId) {
        return accountBalanceService.getAudit(accountId);
    }
}