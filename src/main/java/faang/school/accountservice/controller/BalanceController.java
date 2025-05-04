package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.dto.BalanceRequestDto;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/balance")
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BalanceResponseDto createBalance(@RequestBody BalanceRequestDto request) {
        BalanceResponseDto response = balanceService.createBalance(request);
        log.info("Created new balance: {}", response);
        return response;
    }

    @PutMapping("/update")
    public BalanceResponseDto updateBalance(@RequestBody BalanceRequestDto request) {
        BalanceResponseDto response = balanceService.updateBalance(request);
        log.info("Updated balance: {}", response);
        return response;
    }

    @GetMapping("/{accountNumber}")
    public BalanceResponseDto getBalance(@PathVariable String accountNumber) {
        BalanceResponseDto balanceResponseDto = balanceService.getBalanceByAccountNumber(accountNumber);
        log.info("Get balance for account number: {}", accountNumber);
        return balanceResponseDto;
    }
}
