package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.BalanceResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("api/v1/accounts")
@RestController
@Validated
public class BalanceController {
    private final BalanceService balanceService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/{accountId}/balance")
    public BalanceResponseDto create(
            @Positive(message = "Account id cannot be less than zero")
            @PathVariable
            Long accountId) {
        return balanceService.createBalance(accountId);
    }

    @PutMapping("/{accountId}/balance")
    public BalanceResponseDto update(
            @Positive(message = "Account id cannot be less than zero")
            @PathVariable
            Long accountId,
            @RequestBody
            @Valid
            BalanceUpdateDto balanceUpdateDto) {
        return balanceService.updateBalance(accountId, balanceUpdateDto);
    }

    @GetMapping("/{accountId}/balance")
    public BalanceResponseDto getBalance(
            @Positive(message = "Account id cannot be less than zero")
            @PathVariable
            Long accountId) {
        return balanceService.getBalance(accountId);
    }
}