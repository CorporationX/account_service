package faang.school.accountservice.service;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.dto.PendingDto;

import java.util.List;

public interface BalanceService {

    void create(BalanceDto balanceDto);

    void update(BalanceDto balanceDto);

    void paymentAuthorization(PendingDto pendingDto);

    void clearPayment(List<PendingDto> pendingDto);

    void clearPayment(PendingDto pendingDto);

    BalanceDto getBalance(long accountId);
}
