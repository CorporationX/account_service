package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.entity.Balance;

import java.util.List;

public interface BalanceService {

    void create(BalanceDto balanceDto);

    void update(BalanceDto balanceDto);

    void paymentAuthorization(PendingDto pendingDto);

    void clearPayment(List<PendingDto> pendingDto);

    void clearPayment(PendingDto pendingDto);

    BalanceDto getBalance(long accountId);

    BalanceDto getBalanceByAccountId(long accountId);
}
