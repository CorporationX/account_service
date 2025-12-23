package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.ResponseBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/balance")
@Validated
public class BalanceController {

    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    @GetMapping
    public ResponseBalanceDto getBalance(@RequestParam UUID accountId) {
        return balanceMapper.toDto(balanceService.getBalance(accountId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBalance(@Valid @RequestBody CreateBalanceDto createBalanceDto) {
        balanceService.create(createBalanceDto.accountId(), createBalanceDto.actualAmount());
    }

    @PatchMapping("/account/{accountId}")
    public ResponseBalanceDto updateBalance(@PathVariable UUID accountId,
                                            @Valid @RequestBody UpdateBalanceDto updateBalanceDto) {
        return balanceMapper.toDto(balanceService.update(accountId, updateBalanceDto));
    }
}