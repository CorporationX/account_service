package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.dto.BalanceRequestDto;
import faang.school.accountservice.service.balance.BalanceServiceImpl;
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
    private final BalanceServiceImpl balanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BalanceRequestDto createBalance(@RequestBody BalanceRequestDto request) {
        balanceService.createBalance(request);
        log.info("Created new balance: {}", request);
        return request;
    }

    @PutMapping("/update")
    public BalanceRequestDto updateBalance(@RequestBody BalanceRequestDto request) {
        balanceService.updateBalance(request);
        log.info("Updated balance: {}", request);
        return request;
    }

    @GetMapping("/{accountNumber}")
    public BalanceResponseDto getBalance(@PathVariable String accountNumber) {
       BalanceResponseDto balanceResponseDto = balanceService.getBalanceByAccountNumber(accountNumber);
       log.info("Get balance for account number: {}", accountNumber);
       return balanceResponseDto;
    }
}
