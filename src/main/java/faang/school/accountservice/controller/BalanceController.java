package faang.school.accountservice.controller;


import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("balance/")
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping("{balanceId}")
    public BalanceDto getBalance(@PathVariable Long balanceId) {
        return balanceService.getBalance(balanceId);
    }

}
