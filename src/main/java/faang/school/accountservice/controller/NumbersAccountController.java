package faang.school.accountservice.controller;


import faang.school.accountservice.model.account.FreeAccountNumber;
import faang.school.accountservice.model.account.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
@RestController
public class NumbersAccountController {
    private final FreeAccountNumberService freeAccountNumberService;

    @PostMapping("/generate/{accountType}/{batchSize}")
    public ResponseEntity<String> generateAccountNumbers(
            @PathVariable AccountType accountType,
            @PathVariable int batchSize) {
        freeAccountNumberService.generateAccountNumbers(accountType, batchSize);
        return ResponseEntity.ok("Свободные номера для " + accountType + " сгенерированы");
    }

    @GetMapping("/retrieve/{accountType}")
    public ResponseEntity<FreeAccountNumber> retrieveFreeAccountNumber(@PathVariable AccountType accountType) {
        final FreeAccountNumber[] accountNumber = new FreeAccountNumber[1];
        freeAccountNumberService.retrieveFreeAccountNumber(accountType, number -> accountNumber[0] = number);
        return ResponseEntity.ok(accountNumber[0]);
    }
}