package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.balance.BalanceDto;
import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import org.springframework.stereotype.Component;

@Component
public interface BalanceService {
    BalanceDto createBalance(long accountId);

    BalanceDto updateBalance(BalanceUpdateDto balanceUpdateDto);

    BalanceDto getBalance(long balanceId);

    void deleteBalance(long balanceId);
}
