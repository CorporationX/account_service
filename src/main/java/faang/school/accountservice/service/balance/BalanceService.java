package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Account;
import org.springframework.stereotype.Component;

@Component
public interface BalanceService {
    BalanceDto createBalance(long accountId);

    BalanceDto updateBalance(BalanceDto balanceDto);

    BalanceDto getBalance(long balanceId);

    void deleteBalance(long balanceId);
}
