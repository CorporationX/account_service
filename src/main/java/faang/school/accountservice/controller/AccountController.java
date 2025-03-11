package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountRequest;
import faang.school.accountservice.dto.AccountResponse;
import faang.school.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/getById/{accountId}")
    public AccountResponse getById(@PathVariable Long accountId) {
        return accountService.getDtoById(accountId);
    }

    @GetMapping("/getByOwnerId/{ownerId}")
    public List<AccountResponse> getByOwnerId(@PathVariable Long ownerId) {
        return accountService.findAllByOwnerId(ownerId);
    }

    @PostMapping("/open")
    public ResponseEntity<?> openAccount(@RequestBody AccountRequest accountRequest) {
        accountService.openAccount(accountRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/block/{accountId}")
    public ResponseEntity<?> blockAccount(@PathVariable Long accountId) {
        accountService.block(accountId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/close/{accountId}")
    public ResponseEntity<?> closeAccount(@PathVariable Long accountId) {
        accountService.close(accountId);
        return ResponseEntity.ok().build();
    }
}
