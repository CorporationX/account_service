package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.model.Account;
import org.springframework.stereotype.Component;

@Component
public interface BalanceService {
    void createBalance(Account account);

    BalanceDto updateBalance(BalanceDto balanceDto);

    BalanceDto getBalance(long balanceId);
}
