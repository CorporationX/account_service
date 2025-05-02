package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }

    @PostMapping
    public ResponseEntity<AccountDto> openAccount(@RequestBody @Valid AccountDto accountDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.openAccount(accountDto));
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<AccountDto> blockAccount(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(accountService.blockAccount(id));
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<AccountDto> closeAccount(@PathVariable @Min(1) Long id) {
        return ResponseEntity.ok(accountService.closeAccount(id));
    }
}
