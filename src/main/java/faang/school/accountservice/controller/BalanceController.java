package faang.school.accountservice.controller;

import faang.school.accountservice.converter.BalanceConverter;
import faang.school.accountservice.dto.BalanceOperationRequest;
import faang.school.accountservice.dto.ResponseBalanceDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts/{accountId}/balances")
@Validated
@Slf4j
public class BalanceController {

    private final BalanceConverter balanceConverter;

    @GetMapping
    public ResponseEntity<ResponseBalanceDto> getBalanceByAccountId(@PathVariable @Min(1) Long accountId) {
        log.info("GET request to fetch balance for account ID: {}", accountId);
        return ResponseEntity.ok(balanceConverter.getBalanceByAccountId(accountId));
    }

    @PostMapping("/replenishments")
    public ResponseEntity<ResponseBalanceDto> replenish(
            @PathVariable @Min(1) Long accountId,
            @Valid @RequestBody BalanceOperationRequest request) {
        log.info("POST request to replenish balance for account ID: {}, amount: {}", accountId, request.getAmount());
        return ResponseEntity.ok(balanceConverter.replenish(accountId, request.getAmount()));
    }

    @PostMapping
    public ResponseEntity<ResponseBalanceDto> createBalance(
            @PathVariable @Min(1) Long accountId,
            @Valid @RequestBody BalanceOperationRequest request) {
        log.info("POST request to create balance for account ID: {}, initial amount: {}", accountId, request.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(balanceConverter.createBalance(accountId, request.getAmount()));
    }

    @PostMapping("/authorizations")
    public ResponseEntity<ResponseBalanceDto> authorize(
            @PathVariable @Min(1) Long accountId,
            @Valid @RequestBody BalanceOperationRequest request) {
        log.info("POST request to authorize payment for account ID: {}, amount: {}", accountId, request.getAmount());
        return ResponseEntity.ok(balanceConverter.authorize(accountId, request.getAmount()));
    }

    @PostMapping("/clear")
    public ResponseEntity<ResponseBalanceDto> clear(
            @PathVariable @Min(1) Long accountId,
            @Valid @RequestBody BalanceOperationRequest request) {
        log.info("POST request to clear balance for account ID: {}, amount: {}", accountId, request.getAmount());
        return ResponseEntity.ok(balanceConverter.clear(accountId, request.getAmount()));
    }

    @DeleteMapping("/authorizations")
    public ResponseEntity<ResponseBalanceDto> cancelAuthorization(
            @PathVariable @Min(1) Long accountId,
            @Valid @RequestBody BalanceOperationRequest request) {
        log.info("DELETE request to cancel authorization for account ID: {}, amount: {}", accountId, request.getAmount());
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(balanceConverter.cancelAuthorization(accountId, request.getAmount()));
    }
}