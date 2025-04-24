package faang.school.accountservice.controller;

import faang.school.accountservice.dto.SavingsAccountResponse;
import faang.school.accountservice.service.SavingsAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/savings-accounts")
public class SavingsAccountController {

    private final SavingsAccountService savingsAccountService;

    @PostMapping("/{tariffId}")
    public ResponseEntity<SavingsAccountResponse> openSavingsAccount(@PathVariable Long tariffId) {
        SavingsAccountResponse response = savingsAccountService.openSavingsAccount(tariffId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public SavingsAccountResponse getSavingsAccountById(@PathVariable Long id) {
        return savingsAccountService.getSavingsAccountById(id);
    }

    @GetMapping("/account/{ownerId}")
    public SavingsAccountResponse getSavingsAccountByOwnerId(@PathVariable Long ownerId) {
        return savingsAccountService.getSavingsAccountByOwnerId(ownerId);
    }

    @PatchMapping("/{accountId}/tariff/{tariffId}")
    public ResponseEntity<String> updateTariffOnSavingsAccount(@PathVariable Long accountId,
                                                               @PathVariable Long tariffId) {
        savingsAccountService.updateTariffOnSavingsAccount(accountId, tariffId);
        return ResponseEntity.ok().body("Successfully updated tariff on savings account");
    }
}
