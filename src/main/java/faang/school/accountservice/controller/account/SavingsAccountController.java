package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.SavingsAccountCreatedDto;
import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.service.account.SavingsAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/savingsAccounts")
@RequiredArgsConstructor
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    @PostMapping
    public SavingsAccountDto openSavingsAccount(@RequestBody @Valid SavingsAccountCreatedDto savingsAccountCreatedDto) {
        return savingsAccountService.openSavingsAccount(savingsAccountCreatedDto);
    }

    @GetMapping("/{savingsAccountId}")
    public SavingsAccountDto getSavingsAccountById(@PathVariable @NotNull Long savingsAccountId) {
        return savingsAccountService.getSavingsAccountById(savingsAccountId);
    }

    @GetMapping("/account/{ownerId}")
    public SavingsAccountDto getSavingsAccountByOwnerId(@PathVariable @NotNull Long ownerId) {
        return savingsAccountService.getSavingsAccountByOwnerId(ownerId);
    }
}
