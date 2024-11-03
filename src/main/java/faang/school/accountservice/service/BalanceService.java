package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;

import java.math.BigDecimal;

public interface BalanceService {

    void create(BalanceDto balanceDto);

    void update(BalanceDto balanceDto);

    void updateBalanceWithoutBalanceAudit(long balanceId, BigDecimal newBalance);

    BalanceDto getBalance(long accountId);
}
