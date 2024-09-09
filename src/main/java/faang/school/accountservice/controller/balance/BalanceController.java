package faang.school.accountservice.controller.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balance")
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping
    public BalanceDto createBalance(@RequestBody BalanceDto balanceDto) {
        return balanceService.create(balanceDto);
    }

    @PutMapping("/{id}")
    public BalanceDto updateBalance(@PathVariable Long id, @RequestBody BalanceDto balanceDto) {
        return balanceService.update(id, balanceDto);
    }
}
