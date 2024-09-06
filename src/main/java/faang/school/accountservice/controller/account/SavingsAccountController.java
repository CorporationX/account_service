package faang.school.accountservice.controller.account;

import faang.school.accountservice.dto.account.SavingsAccountDto;
import faang.school.accountservice.dto.account.TariffAndRateDto;
import faang.school.accountservice.dto.account.TariffDto;
import faang.school.accountservice.service.account.SavingsAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/savings-accounts")
@RequiredArgsConstructor
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SavingsAccountDto savingsAccountDto(@RequestBody @Valid SavingsAccountDto savingsAccountDto,
                                               @RequestParam String tariff) {
        return savingsAccountService.create(savingsAccountDto, tariff);
    }

    @PutMapping("/{savingsAccountId}/history")
    @ResponseStatus(HttpStatus.OK)
    public SavingsAccountDto updateSavingsAccountHistory(@PathVariable UUID savingsAccountId,
                                         @RequestParam String tariff) {
        return savingsAccountService.updateSavingsAccountHistory(savingsAccountId, tariff);
    }

    @GetMapping("/{savingsAccountId}")
    @ResponseStatus(HttpStatus.OK)
    public TariffAndRateDto getTariffAndRateById(@PathVariable UUID savingsAccountId) {
        return savingsAccountService.getSavingAccountById(savingsAccountId);
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public TariffAndRateDto getTariffAndRateByUserId(@PathVariable long userId) {
        return savingsAccountService.getSavingAccountByUserId(userId);
    }
}
