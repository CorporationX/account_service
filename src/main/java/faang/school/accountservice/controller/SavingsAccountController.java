package faang.school.accountservice.controller;

import faang.school.accountservice.dto.SavingsAccountDto;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/savings_account")
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;

    @PostMapping("/{accountId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto createSavingAccount(@PathVariable Long accountId,
                                                 @RequestParam Long tariffId) {
        return savingsAccountService.createSavingAccount(accountId, tariffId);
    }

    @GetMapping("/{id}")
    public SavingsAccountDto getSavingsAccountById(@PathVariable Long id) {
        return savingsAccountService.getSavingsAccountById(id);
    }

    @GetMapping("/{accountId}")
    public SavingsAccountDto getSavingsAccountByAccountId(@PathVariable Long accountId) {
        return savingsAccountService.getSavingsAccountByAccountId(accountId);
    }
}
