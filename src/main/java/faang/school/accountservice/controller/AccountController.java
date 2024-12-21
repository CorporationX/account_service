package faang.school.accountservice.controller;

import faang.school.accountservice.model.account.freeaccounts.AccountType;
import faang.school.accountservice.repository.account.freeaccounts.FreeAccountRepository;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final FreeAccountNumberService freeAccountNumberService;

    @GetMapping("/accounts/retrieve/{type}")
    public ResponseEntity<String> retrieveAccountNumber(@PathVariable("type") String type) {
        AccountType accountType;

        try {
            accountType = AccountType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid account type: " + type + " .Valid types are: CREDIT DEBIT");
        }
        freeAccountNumberService.generateOneAccountNumber(accountType);

        StringBuilder result = new StringBuilder();
        freeAccountNumberService.retrieveAccountNumber(accountType, accountNumber ->
                result.append("Полученный номер счета: ").append(accountNumber.getId().getAccountNumber()));
        return ResponseEntity.ok(result.toString());
    }
}
