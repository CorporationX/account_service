package faang.school.accountservice.converter;

import faang.school.accountservice.dto.ResponseBalanceDto;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class BalanceConverter {

    private final BalanceService balanceService;
    private final BalanceMapper balanceMapper;

    public ResponseBalanceDto getBalanceByAccountId(Long accountId) {
        return balanceMapper.toResponseBalanceDto(balanceService.getBalanceByAccountId(accountId));
    }

    public ResponseBalanceDto replenish(Long accountId, BigDecimal amount) {
        return balanceMapper.toResponseBalanceDto(balanceService.replenish(accountId, amount));
    }

    public ResponseBalanceDto createBalance(Long accountId, BigDecimal amount) {
        return balanceMapper.toResponseBalanceDto(balanceService.createBalance(accountId, amount));
    }

    public ResponseBalanceDto authorize(Long accountId, BigDecimal amount) {
        return balanceMapper.toResponseBalanceDto(balanceService.authorize(accountId, amount));
    }

    public ResponseBalanceDto clear(Long accountId, BigDecimal amount, BigDecimal authorizedAmount) {
        return balanceMapper.toResponseBalanceDto(balanceService.clear(accountId, amount, authorizedAmount));
    }

    public ResponseBalanceDto cancelAuthorization(Long accountId, BigDecimal amount) {
        return balanceMapper.toResponseBalanceDto(balanceService.cancelAuthorization(accountId, amount));
    }
}
