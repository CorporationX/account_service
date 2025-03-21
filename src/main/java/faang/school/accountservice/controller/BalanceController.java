package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.BalanceCreateDto;
import faang.school.accountservice.dto.balance.BalanceReadDto;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/balance")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/{balanceId}")
    public BalanceReadDto getBalanceDto(@PathVariable long balanceId) {
        return balanceService.getBalanceDto(balanceId);
    }

    @PutMapping
    public BalanceReadDto createBalance(@RequestBody BalanceCreateDto createDto) {
        return balanceService.createBalance(createDto);
    }

    @PatchMapping("/{balanceId}/increase")
    public BalanceReadDto increaseBalance(@PathVariable long balanceId, @RequestParam BigDecimal amount) {
        return balanceService.increaseBalance(balanceId, amount);
    }

    @PatchMapping("/{balanceId}/decrease")
    public BalanceReadDto decreaseBalance(@PathVariable long balanceId, @RequestParam BigDecimal amount) {
        return balanceService.decreaseBalance(balanceId, amount);
    }

    @PatchMapping("/{balanceId}/reserve")
    public BalanceReadDto reserveBalance(@PathVariable long balanceId, @RequestParam BigDecimal amount) {
        return balanceService.reserveBalance(balanceId, amount);
    }

    @PatchMapping("/{balanceId}/release")
    public BalanceReadDto releaseReservedBalance(@PathVariable long balanceId, @RequestParam BigDecimal amount) {
        return balanceService.releaseReservedBalance(balanceId, amount);
    }

    @PatchMapping("/{balanceId}")
    public BalanceReadDto cancelBalanceReservation(@PathVariable long balanceId) {
        return balanceService.cancelBalanceReservation(balanceId);
    }
}
