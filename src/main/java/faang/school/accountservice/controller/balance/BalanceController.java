package faang.school.accountservice.controller.balance;

import faang.school.accountservice.controller.facade.BalanceFacade;
import faang.school.accountservice.dto.balance.BalanceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceFacade balanceFacade;

    @GetMapping("/{accountId}")
    public BalanceDto getBalance(@PathVariable UUID accountId) {
        return balanceFacade.getBalance(accountId);
    }

    @PostMapping("/{accountId}/authorize")
    public BalanceDto authorize(@PathVariable UUID accountId,
                                @RequestParam BigDecimal amount) {
        return balanceFacade.authorize(accountId, amount);
    }

    @PostMapping("/{accountId}/clear")
    public BalanceDto clear(@PathVariable UUID accountId,
                            @RequestParam BigDecimal amount) {
        return balanceFacade.clear(accountId, amount);
    }

    @PostMapping("/{accountId}/cancel")
    public BalanceDto cancel(@PathVariable UUID accountId,
                             @RequestParam BigDecimal amount) {
        return balanceFacade.cancelAuthorization(accountId, amount);
    }
}
