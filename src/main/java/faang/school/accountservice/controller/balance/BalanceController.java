package faang.school.accountservice.controller.balance;

import faang.school.accountservice.balance.service.BalanceService;
import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.balance.BalanceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account/{accountId}/balance")
public class BalanceController {

    private final BalanceService balanceService;
    private final UserContext userContext;

    @GetMapping("/{balanceId}")
    public BalanceDto getBalance(@PathVariable("balanceId") Long balanceId) {
        log.info("Request received to get balance with id: {}", balanceId);
        Long userId = userContext.getUserId();
        return balanceService.getBalance(balanceId, userId);
    }
}
