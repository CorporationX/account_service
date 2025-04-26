package faang.school.accountservice.controller;

import faang.school.accountservice.dto.AccountRequestDto;
import faang.school.accountservice.dto.AccountResponseDto;
import faang.school.accountservice.service.account.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDto> getAccount(
            @PathVariable @Valid @NotNull @NotBlank String accountNumber) {

        log.info("Received request to get account with ID {}", accountNumber);
        return ResponseEntity.ok(accountService.getAccount(accountNumber));
    }

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(@Valid @RequestBody AccountRequestDto accountRequest) {
        log.info("Received request to create account with ID {}", accountRequest);
        return ResponseEntity.ok(accountService.createAccount(accountRequest));
    }

    @PutMapping("/{accountNumber}/block")
    public ResponseEntity<AccountResponseDto> blockAccount(
            @PathVariable @Valid @NotNull @NotBlank String accountNumber) {

        log.info("Received request to block account with ID {}", accountNumber);
        return ResponseEntity.ok(accountService.blockAccount(accountNumber));
    }

    @PutMapping("/{accountNumber}/close")
    public ResponseEntity<AccountResponseDto> closeAccount(
            @PathVariable @Valid @NotNull @NotBlank String accountNumber) {

        log.info("Received request to close account with ID {}", accountNumber);
        return ResponseEntity.ok(accountService.closeAccount(accountNumber));
    }
}
