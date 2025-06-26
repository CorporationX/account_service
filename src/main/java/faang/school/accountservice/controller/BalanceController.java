package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/{id}")
    public BalanceDto getBalance(@PathVariable Long id) {
        return balanceService.getBalance(id);
    }

    @PostMapping
    public BalanceDto create(@RequestBody @Valid BalanceDto request) {
        return balanceService.create(request);
    }

    @PutMapping("{/id}")
    public BalanceDto update(@PathVariable Long id, @RequestBody BalanceDto balanceDto) {
        return balanceService.update(id, balanceDto);
    }
}
