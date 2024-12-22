package faang.school.accountservice.controller;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account-numbers")
@RequiredArgsConstructor
public class AccountNumbersController {

    private final FreeAccountNumbersService freeAccountNumbersService;

    @PostMapping("/free/{accountType}")
    public ResponseEntity<String> getFreeAccountNumber(@PathVariable AccountType accountType) {
        freeAccountNumbersService.getAndRemoveFreeAccountNumber(accountType, accountNumber -> {
        });
        return ResponseEntity.ok("Номер счета успешно получен");
    }
}
