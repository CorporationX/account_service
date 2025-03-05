package faang.school.accountservice.controller;

import faang.school.accountservice.dto.savings_account.CreateSavingsAccountRequest;
import faang.school.accountservice.dto.savings_account.SavingsAccountDto;
import faang.school.accountservice.dto.savings_account.UpdateTariffSavingsAccountRequest;
import faang.school.accountservice.service.SavingsAccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/savings-account")
@RequiredArgsConstructor
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    @PostMapping
    public SavingsAccountDto createSavingsAccount(@Valid @RequestBody CreateSavingsAccountRequest createSavingsAccountRequest) {
        return savingsAccountService.create(createSavingsAccountRequest);
    }

    @PatchMapping("/tariff")
    public SavingsAccountDto addTariffAccount(@Valid @RequestBody UpdateTariffSavingsAccountRequest updateTariffSavingsAccountRequest){
        return savingsAccountService.addTariff(updateTariffSavingsAccountRequest);
    }

    @GetMapping("/{id}")
    public SavingsAccountDto getById(@Valid @Positive @NotNull @PathVariable Long id) {
        return savingsAccountService.getById(id);
    }

    @GetMapping("/{accountId}")
    public SavingsAccountDto getByAccountId(@Valid @Positive @NotNull @PathVariable Long accountId) {
        return savingsAccountService.getByAccountId(accountId);
    }
}

