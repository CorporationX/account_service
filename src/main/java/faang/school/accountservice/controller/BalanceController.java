package faang.school.accountservice.controller;


import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balance")
public class BalanceController {

    private final BalanceService balanceService;


    @PostMapping
    public BalanceDto saveBalance(@RequestBody BalanceDto balanceDto) {
        return balanceService.saveBalance(balanceDto);
    }

    @PutMapping
    public void updateBalance(@RequestBody BalanceDto balanceDto) {
        if (balanceDto.getAuthBalance() == null) {
            throw new IllegalArgumentException("auth balance is null");
        }
        balanceService.updateBalance(balanceDto.getAccountId(), balanceDto.getAuthBalance());
    }
}
