package faang.school.accountservice.controller;


import faang.school.accountservice.model.account.FreeAccountNumber;
import faang.school.accountservice.model.account.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/account")
@RequiredArgsConstructor
@RestController
public class NumbersAccountController {
    private final FreeAccountNumberService freeAccountNumberService;

    @PostMapping("/generate/{accountType}/{batchSize}")
    public String generateAccountNumbers(
            @PathVariable AccountType accountType,
            @PathVariable int batchSize) {
        freeAccountNumberService.generateAccountNumbers(accountType, batchSize);
        return "Свободные номера для " + accountType + " сгенерированы";
    }

    @GetMapping("/retrieve/{accountType}")
    public FreeAccountNumber retrieveFreeAccountNumber(@PathVariable AccountType accountType) {
        final FreeAccountNumber[] accountNumber = new FreeAccountNumber[1];
        freeAccountNumberService.retrieveFreeAccountNumber(accountType, number -> accountNumber[0] = number);
        return accountNumber[0];
    }
}