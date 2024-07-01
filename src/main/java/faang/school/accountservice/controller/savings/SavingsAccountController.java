package faang.school.accountservice.controller.savings;

import faang.school.accountservice.dto.OpenSavingsAccountRequest;
import faang.school.accountservice.model.SavingsAccount;
import faang.school.accountservice.service.savings.SavingsAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/savings-accounts")
public class SavingsAccountController {

    private SavingsAccountService savingsAccountService;

    @PostMapping()
    public SavingsAccount openSavingsAccount(@RequestBody OpenSavingsAccountRequest request) {
        return savingsAccountService.openSavingsAccount(request.getAccountId(), request.getInitialTariffId());
    }

    @GetMapping("/{id}")
    public SavingsAccount getSavingsAccount(@PathVariable Long id) {
        return savingsAccountService.getSavingsAccount(id);
    }

    @GetMapping("/client/{clientId}")
    public SavingsAccount getSavingsAccountByClientId(@PathVariable Long clientId) {
        return savingsAccountService.getSavingsAccountByClientId(clientId);
    }
}
