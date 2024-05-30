package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balance")
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping
    public void create(@RequestBody Account account) {
        balanceService.createBalance(account);
    }

    @PutMapping("/writeOffClearing")
    public BalanceDto writeOffClearingBalance(@RequestBody BalanceDto balanceDto) {
        return balanceService.writeOffClearingBalance(balanceDto);
    }

    @GetMapping
    public BalanceDto getBalance(@RequestBody long id) {
        return balanceService.getBalance(id);
    }

    @PutMapping("/hold")
    public BalanceDto holdAuthorizationBalance(@RequestBody BalanceDto balanceDto) {
        return balanceService.writeOffAuthorizationBalance(balanceDto);
    }

    @PutMapping("/adding")
    public BalanceDto addingBalance(@RequestBody BalanceDto balanceDto) {
        return balanceService.addingToBalance(balanceDto);
    }
}
