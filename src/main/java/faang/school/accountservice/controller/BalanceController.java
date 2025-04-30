package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.exception.AccessException;
import faang.school.accountservice.exception.NotEnoughFundsException;
import faang.school.accountservice.exception.SelfPayException;
import faang.school.accountservice.exception.WrongAmountException;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/balances")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService service;

    @GetMapping("/{balanceId}")
    public ResponseEntity<BalanceResponseDto> getBalance(@PathVariable Long balanceId) {
        BalanceResponseDto response = service.getBalance(balanceId);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/{balanceId}/deposit/{amount}")
    public void depositFunds(@PathVariable Long balanceId,
                             @PathVariable @Positive BigDecimal amount,
                             @RequestParam(required = false) String comment) {
        service.addFunds(balanceId, amount, comment);
    }

    @PostMapping("/{balanceId}/withdraw/{amount}")
    public void withdrawFunds(@PathVariable Long balanceId,
                             @PathVariable @Positive BigDecimal amount,
                             @RequestParam(required = false) String comment) {
        service.withdrawFunds(balanceId, amount, comment);
    }

    @PostMapping("/{senderId}/send/{receiverId}/{amount}")
    public void sendPayment(@PathVariable  Long senderId,
                            @PathVariable Long receiverId,
                            @PathVariable @Positive BigDecimal amount,
                            @RequestParam(required = false) String comment) {
        service.sendPayment(senderId, receiverId, amount, comment);
    }

}
