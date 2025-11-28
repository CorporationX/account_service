package faang.school.accountservice.controller.balance;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.CreateBalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.service.BalanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/balance")
@RequiredArgsConstructor
public class BalanceController {
    private final BalanceService balanceService;
    private final UserContext userContext;

    @PostMapping
    public BalanceDto create(@RequestBody @Valid CreateBalanceDto createBalanceDto) {
        return balanceService.create(userContext.getUserId(), createBalanceDto);
    }

    @PutMapping("/{balanceId}")
    public BalanceDto update(@PathVariable Long balanceId, @RequestBody @Valid UpdateBalanceDto updateBalanceDto) {
        return balanceService.update(userContext.getUserId(), balanceId, updateBalanceDto);
    }

    @GetMapping("/{balanceId}")
    public BalanceDto getById(@PathVariable Long balanceId) {
        return balanceService.getById(userContext.getUserId(), balanceId);
    }

    @DeleteMapping("/{balanceId}")
    public void delete(@PathVariable Long balanceId) {
        balanceService.delete(userContext.getUserId(), balanceId);
    }
}
