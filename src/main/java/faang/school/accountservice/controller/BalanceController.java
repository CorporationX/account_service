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

    @PutMapping("/plus/{balanceId}/{operationId}")
    public BalanceDto plusBalance(@PathVariable("balanceId") Long balanceId,
                                  @PathVariable("operationId") String operationId,
                                  @RequestBody Double money) {
        return balanceService.plusBalance(balanceId, money, operationId);
    }

    @PutMapping("/auth/{balanceId}/{operationId}")
    public BalanceDto authBalance(@PathVariable("balanceId") Long balanceId,
                                  @PathVariable("operationId") String operationId,
                                  @RequestBody Double money) {
        return balanceService.authBalance(balanceId, money, operationId);
    }

    @PutMapping("/clearing/all/{balanceId}/{operationId}")
    public BalanceDto clearingBalanceAll(@PathVariable("balanceId") Long balanceId,
                                         @PathVariable("operationId") String operationId) {
        return balanceService.clearingBalance(balanceId, operationId);
    }

    @PutMapping("/clearing/part/{balanceId}/{operationId}")
    public BalanceDto clearingBalancePart(@PathVariable("balanceId") Long balanceId,
                                          @PathVariable("operationId") String operationId,
                                          @RequestBody Double money) {
        return balanceService.clearingBalance(balanceId, money, operationId);
    }

    @PutMapping("/cancel/{balanceId}/{operationId}")
    public BalanceDto cancelBalance(@PathVariable("balanceId") Long balanceId,
                                    @PathVariable("operationId") String operationId) {
        return balanceService.cancelBalance(balanceId, operationId);
    }

}
