package faang.school.accountservice.controller;

import faang.school.accountservice.dto.savings.SavingsAccountDto;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/savings-accounts")
@RequiredArgsConstructor
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;

    @PostMapping("/open/{accountId}")
    public void openSavingsAccount(@PathVariable UUID accountId,
                                   @RequestParam UUID tariffId) {
        savingsAccountService.openSavingsAccount(accountId, tariffId);
    }

    @GetMapping("/{id}")
    public SavingsAccountDto getSavingsAccountById(@PathVariable UUID id) {
        return savingsAccountService.getSavingsAccountById(id);
    }

    @GetMapping("/owner/{ownerId}")
    public SavingsAccountDto getSavingsAccountByOwnerId(@PathVariable UUID ownerId) {
        return savingsAccountService.getSavingsAccountByOwnerId(ownerId);
    }
}
