package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceOperationDto;
import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.service.interfaces.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
@Slf4j
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponseDto> getBalance(@PathVariable long accountId,
                                                         @RequestHeader("x-user-id") long userId) {
        validateAccountId(accountId);
        return ResponseEntity.ok(balanceService.getBalance(accountId));
    }

    @PostMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponseDto> createBalance(@PathVariable long accountId,
                                                            @RequestHeader("x-user-id") long userId) {
        validateAccountId(accountId);
        BalanceResponseDto response = balanceService.createBalance(accountId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{responseId}")
                .buildAndExpand(response.getBalanceId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponseDto> updateBalance(@PathVariable long accountId,
                                                            @RequestHeader("x-user-id") long userId,
                                                            @RequestBody @Valid BalanceOperationDto balanceOperationDto) {
        validateAccountIdWithDto(accountId, balanceOperationDto.getAccountId());
        return ResponseEntity.ok(balanceService.updateBalance(balanceOperationDto));
    }

    private void validateAccountId(long accountId) {
        if (accountId <= 0) {
            log.error("Invalid account ID: {}", accountId);
            throw new IllegalArgumentException("Account ID must be a positive number");
        }
    }

    private void validateAccountIdWithDto(long pathAccountId, long dtoAccountId) {
        validateAccountId(pathAccountId);
        validateAccountId(dtoAccountId);
        if (pathAccountId != dtoAccountId) {
            log.error("Account ID mismatch: pathAccountId={}, dtoAccountId={}", pathAccountId, dtoAccountId);
            throw new IllegalArgumentException("Account ID in path and DTO must match");
        }
    }
}
