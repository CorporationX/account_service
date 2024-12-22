package faang.school.accountservice.controller;


import faang.school.accountservice.model.account.freeaccounts.FreeAccountNumber;
import faang.school.accountservice.service.FreeAccountNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final FreeAccountNumberService freeAccountNumberService;

    @PostMapping("/process/{type}")
    public ResponseEntity<String> processAccountNumber(@PathVariable("type") String type) {
        try {
            freeAccountNumberService.processAccountNumber(type, accountNumber -> {
                System.out.println("Processed account number: " + accountNumber.getAccountNumber());
            });
            return ResponseEntity.ok("Account processed successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}