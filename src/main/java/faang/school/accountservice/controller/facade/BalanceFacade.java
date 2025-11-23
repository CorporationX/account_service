package faang.school.accountservice.controller.facade;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.UpdateBalanceDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BalanceFacade {

    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    public BalanceDto getBalance(Long accountId) {
        return balanceMapper.toDto(balanceService.getBalance(accountId));
    }

    public BalanceDto createBalance(UUID accountId) {
        return balanceMapper.toDto(balanceService.createBalance(accountId));
    }

    public BalanceDto updateBalance(Long accountId, UpdateBalanceDto dto) {
        Balance balance = balanceService.updateBalance(
                accountId,
                dto.authorizedBalance(),
                dto.actualBalance()
        );
        return balanceMapper.toDto(balance);
    }
}
