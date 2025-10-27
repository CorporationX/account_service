package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountNumberResponse;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/account-numbers")
@RequiredArgsConstructor
public class AccountNumberController {

    private final FreeAccountNumberService freeAccountNumberService;

    @PostMapping("/generate")
    public ResponseEntity<String> generateAccountNumbers(
            @RequestParam AccountType type,
            @RequestParam(defaultValue = "100") int batchSize) {

        log.info("Received request to generate {} account numbers for type: {}", batchSize, type);

        freeAccountNumberService.generateAccountNumbers(type, batchSize);

        return ResponseEntity.ok(
                String.format("Successfully generated %d account numbers for type: %s", batchSize, type));
    }

    @PostMapping("/retrieve")
    public ResponseEntity<AccountNumberResponse> retrieveAccountNumber(
            @RequestParam AccountType type) {

        log.info("Received request to retrieve account number for type: {}", type);

        Long accountNumber = freeAccountNumberService.retrieveAccountNumber(type);
        return ResponseEntity.ok(new AccountNumberResponse(accountNumber, type));
    }
}
