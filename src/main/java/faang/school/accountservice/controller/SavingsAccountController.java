package faang.school.accountservice.controller;

import faang.school.accountservice.dto.TariffDto;
import faang.school.accountservice.enums.TariffType;
import faang.school.accountservice.service.SavingsAccountService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author Evgenii Malkov
 */
@RestController
@RequestMapping("v1/savings-account")
@RequiredArgsConstructor
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    @GetMapping
    public TariffDto getSavingsAccountTariffByClientId(@RequestParam long clientId) {
        return savingsAccountService.getSavingsAccountTariffByClientId(clientId);
    }

    @GetMapping("/{accountId}")
    public TariffDto getSavingsAccountTariffByAccountId(@PathVariable long accountId) {
        return savingsAccountService.getSavingsAccountTariffByAccountId(accountId);
    }

    @PutMapping("/{accountId}")
    public void updateSavingsAccountTariff(@PathVariable long accountId,
                                           @NotNull @RequestParam TariffType tariffType) {
        savingsAccountService.updateSavingsAccountTariff(accountId, tariffType);
    }
}
