package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.BalanceDto;
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
@RequiredArgsConstructor
@RequestMapping("/balance")
public class BalanceController {

    private final BalanceService balanceService;

    @PostMapping("/{id}")
    public BalanceDto createBalance(@PathVariable("id") Long accountId) {
        return balanceService.createBalance(accountId);
    }

    @GetMapping("/{id}")
    public BalanceDto getBalance(@PathVariable("id") Long balanceId) {
        return balanceService.getBalanceById(balanceId);
    }

    @PutMapping("/plus/{id}")
    public BalanceDto plusBalance(@PathVariable("id") Long balanceId, @RequestBody Double money) {
        return balanceService.plusBalance(balanceId, money);
    }

    @PutMapping("/auth/{id}")
    public BalanceDto authBalance(@PathVariable("id") Long balanceId, @RequestBody Double money) {
        return balanceService.authBalance(balanceId, money);
    }

    @PutMapping("/clearing/all/{id}")
    public BalanceDto clearingBalanceAll(@PathVariable("id") Long balanceId) {
        return balanceService.clearingBalanceAllSum(balanceId);
    }

    @PutMapping("/clearing/part/{id}")
    public BalanceDto clearingBalancePart(@PathVariable("id") Long balanceId, @RequestBody Double money) {
        return balanceService.clearingBalancePartSum(balanceId, money);
    }

    @PutMapping("/cancel/{id}")
    public BalanceDto cancelBalance(@PathVariable("id") Long balanceId) {
        return balanceService.cancelBalance(balanceId);
    }

}
