package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.dto.AccountFilterDto;
import faang.school.accountservice.entity.Account;
import faang.school.accountservice.service.AccountService;
import faang.school.accountservice.validator.AccountValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
@Slf4j
public class AccountController {
    private final AccountService accountService;
    private final AccountValidator accountValidator;

    @PostMapping("/open")
    public ResponseEntity<AccountDto> createAccount(@RequestBody AccountDto accountDto) {
        accountValidator.validateAccountOwner(accountDto);
        return ResponseEntity.ok(accountService.createAccount(accountDto));
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<AccountDto> blockAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.blockAccount(id));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<AccountDto> closeAccount(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.closeAccount(id));
    }

    @PostMapping("/filter")
    public ResponseEntity<List<AccountDto>> getAccountsWithFilters(@RequestBody AccountFilterDto accountFilterDto) {
        return ResponseEntity.ok(accountService.getAccountsWithFilters(accountFilterDto));
    }
}