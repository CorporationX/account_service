package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
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
    private final BalanceService service;

    @GetMapping("/{balanceId}")
    public BalanceDto getBalance(@PathVariable Long balanceId) {
        return service.getBalanceById(balanceId);
    }

    @PostMapping
    public BalanceDto createBalance(@RequestBody BalanceDto balanceDto) {
        return service.createBalance(balanceDto);
    }

    @PutMapping()
    public BalanceDto updateBalance(@RequestBody BalanceDto balanceDto) {
       return service.updateBalance(balanceDto);
    }
}
