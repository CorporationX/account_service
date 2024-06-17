package faang.school.accountservice.controller.balance;


import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/balance")
public class BalanceController {
    private final BalanceService balanceService;

    @GetMapping("/{balanceId}")
    public BalanceDto getBalance(@PathVariable @NotNull Long balanceId) {
        return balanceService.getBalance(balanceId);
    }
}
