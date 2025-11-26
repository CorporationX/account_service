package faang.school.accountservice.controller.facade;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BalanceFacade {

    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    public BalanceDto getBalance(UUID accountId) {
        return balanceMapper.toDto(balanceService.getBalance(accountId));
    }

    public BalanceDto authorize(UUID accountId, BigDecimal amount) {
        Balance balance = balanceService.authorize(accountId, amount);
        return balanceMapper.toDto(balance);
    }

    public BalanceDto clearing(UUID accountId, BigDecimal amount) {
        Balance balance = balanceService.clearing(accountId, amount);
        return balanceMapper.toDto(balance);
    }

    public BalanceDto cancelAuthorization(UUID accountId, BigDecimal amount) {
        Balance balance = balanceService.cancelAuthorization(accountId, amount);
        return balanceMapper.toDto(balance);
    }
}
