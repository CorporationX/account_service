package faang.school.accountservice.controller;

import faang.school.accountservice.dto.savingsAccounts.SavingsAccountCreateDto;
import faang.school.accountservice.dto.savingsAccounts.SavingsAccountReadDto;
import faang.school.accountservice.service.savings.SavingsAccountScheduler;
import faang.school.accountservice.service.savings.SavingsAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/savings-accounts")
public class SavingsAccountController {
    private final SavingsAccountService savingsAccountService;
    private final SavingsAccountScheduler savingsAccountScheduler;

    @PostMapping
    public SavingsAccountReadDto openSavingsAccount(@RequestBody @Valid SavingsAccountCreateDto dto) {
        return savingsAccountService.openSavingsAccount(dto);
    }

    @GetMapping("/{accountId}")
    public SavingsAccountReadDto getSavingsAccount(@PathVariable Long accountId) {
        return savingsAccountService.getSavingsAccount(accountId);
    }

    @GetMapping("/client/{clientId}")
    public SavingsAccountReadDto getSavingsAccountByClientId(@PathVariable Long clientId) {
        return savingsAccountService.getSavingsAccountByClientId(clientId);
    }

    @PostMapping("/{accountId}/deposit")
    public String deposit(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        savingsAccountScheduler.deposit(accountId, amount);
        return "Пополнение счета успешно выполнено";
    }

    @PostMapping("/{accountId}/withdraw")
    public String withdraw(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        savingsAccountScheduler.withdraw(accountId, amount);
        return "Снятие средств успешно выполнено";
    }
}
