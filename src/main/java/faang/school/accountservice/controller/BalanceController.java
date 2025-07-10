package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.balance.BalanceMapper;
import faang.school.accountservice.service.BalanceService;
import faang.school.accountservice.validation.ValidAccountNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;


@RestController
@RequestMapping("/api/v1/balances")
@RequiredArgsConstructor
@Validated
@Log4j2
public class BalanceController {

    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    @GetMapping("/accounts/uuids/{accountUUID}")
    public ResponseEntity<BalanceDto> getBalanceByUUID(@PathVariable UUID accountUUID) {
        Balance balance = balanceService.getBalanceByAccountId(accountUUID);
        BalanceDto balanceDto = balanceMapper.toDto(balance);
        return ResponseEntity.ok(balanceDto);
    }

    @GetMapping("/accounts/numbers/{accountNumber}")
    public ResponseEntity<BalanceDto> getBalanceByAccountNumber(@PathVariable @ValidAccountNumber String accountNumber) {
        Balance balance = balanceService.getBalanceByAccountNumber(accountNumber);
        BalanceDto balanceDto = balanceMapper.toDto(balance);
        return ResponseEntity.ok(balanceDto);
    }
}