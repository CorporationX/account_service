package faang.school.accountservice.controller;

import faang.school.accountservice.model.account.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/free-accounts")
@RequiredArgsConstructor
@Validated
public class FreeAccountNumbersController {
    private final FreeAccountNumberService freeAccountNumberService;

    @PostMapping("/batchSize")
    public void generateFreeAccountNumbers(@RequestParam("accountType") AccountType accountType,
                                           @RequestParam("accountLength") Long accountLength,
                                           @RequestParam("quantity") long quantity) {
        freeAccountNumberService.generateFreeAccountNumbersWithBatchSize(accountType, accountLength, quantity);
    }

    @PostMapping("/limit")
    public void generateFreeAccountNumbersToLimit(@RequestParam("accountType") AccountType accountType,
                                                  @RequestParam("accountLength") Long accountLength,
                                                  @RequestParam("limit") long limit) {
        freeAccountNumberService.generateFreeAccountNumbersWithLimit(accountType, accountLength, limit);
    }
}
