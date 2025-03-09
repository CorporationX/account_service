package faang.school.accountservice.controller;

import faang.school.accountservice.dto.balance.BalanceCreateRequestDto;
import faang.school.accountservice.dto.balance.BalanceCreateResponseDto;
import faang.school.accountservice.dto.balance.BalanceUpdateRequestDto;
import faang.school.accountservice.dto.balance.BalanceUpdateResponseDto;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/balances")
public class BalanceController {
    private final BalanceService balanceService;

    @PostMapping
    public BalanceCreateResponseDto createBalance(
            @Valid @RequestBody BalanceCreateRequestDto balanceCreateRequestDto) {
        return balanceService.createBalance(balanceCreateRequestDto);
    }

    @PutMapping
    public BalanceUpdateResponseDto updateBalance(
            @Valid @RequestBody BalanceUpdateRequestDto balanceUpdateRequestDto) {
        return balanceService.updateBalance(balanceUpdateRequestDto);
    }
}
