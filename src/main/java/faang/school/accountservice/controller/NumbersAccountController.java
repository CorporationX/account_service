package faang.school.accountservice.controller;

import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumbersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Slf4j
public class NumbersAccountController {

    private final FreeAccountNumbersService freeAccountNumbersService;

    @PostMapping("/generate/{accountType}")
    public ResponseEntity<String> generateFreeAccountNumbers(
        @PathVariable AccountType accountType,
        @RequestParam int batchSize) {
        log.info("Запрос на генерацию {} свободных номеров счетов для типа: {}", batchSize, accountType);
        freeAccountNumbersService.generateFreeAccountNumbers(accountType, batchSize);
        log.info("Номера счетов успешно сгенерированы для типа: {}", accountType);
        return ResponseEntity.ok("Номера счетов успешно сгенерированы.");
    }

    @GetMapping("/retrieve/{accountType}")
    public ResponseEntity<String> retrieveFreeAccountNumbers(@PathVariable AccountType accountType) {
        log.info("Запрос на получение первого свободного номера счета для типа: {}", accountType);
        freeAccountNumbersService.retrieveFreeAccountNumbers(accountType, freeAccountNumber -> {
            String accountNumber = String.valueOf(freeAccountNumber.getFreeAccountId().getAccountNumber());
            log.info("Свободный номер счета для типа {}: {}", accountType, accountNumber);
        });
        log.info("Номер счета успешно получен для типа: {}", accountType);
        return ResponseEntity.ok("Номер счета успешно получен.");
    }
}
