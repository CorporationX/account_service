package faang.school.accountservice.controller;

import faang.school.accountservice.dto.BalanceResponseDto;
import faang.school.accountservice.service.balance.BalanceService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/balances")
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping(path = "/{balanceId}")
    BalanceResponseDto getBalance(@NotBlank @PathVariable Long balanceId) {
        return balanceService.getBalance(balanceId);
    }
}
