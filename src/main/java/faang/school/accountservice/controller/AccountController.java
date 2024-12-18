package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> openAccount(@RequestBody AccountRequest request) {
        return ResponseEntity.ok(accountService.openAccount(request));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<AccountResponse> blockAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.blockAccount(id));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<AccountResponse> closeAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.closeAccount(id));
    }
}
