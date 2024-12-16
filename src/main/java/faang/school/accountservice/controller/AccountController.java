package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }

    @PostMapping("/open")
    public ResponseEntity<AccountDto> openAccount(@Valid @RequestBody AccountDto accountDto) {
        return ResponseEntity.ok(accountService.openAccount(accountDto));
    }

    @PostMapping("/{accountId}/block")
    public ResponseEntity<AccountDto> blockAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.blockAccount(accountId));
    }

    @PostMapping("/{accountId}/close")
    public ResponseEntity<AccountDto> closeAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.closeAccount(accountId));
    }
}
