package faang.school.accountservice.mapper;

import faang.school.accountservice.model.balance.Balance;
import faang.school.accountservice.model.balance.BalanceAudit;
import org.springframework.stereotype.Component;

@Component
public class BalanceAuditMapper {

    public BalanceAudit toBalanceAudit(Balance balance) {
        return BalanceAudit.builder()
                .accountNumber(balance.getAccount().getAccountNumber())
                .version(balance.getVersion())
                .authorizationBalance(balance.getAuthorization())
                .actualBalance(balance.getActual())
                .build();
    }
}
