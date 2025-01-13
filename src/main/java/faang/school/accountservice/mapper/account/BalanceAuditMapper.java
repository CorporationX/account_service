package faang.school.accountservice.mapper.account;

import faang.school.accountservice.entity.account.Balance;
import faang.school.accountservice.entity.account.BalanceAudit;
import org.springframework.stereotype.Component;

@Component
public class BalanceAuditMapper {

    public BalanceAudit toBalanceAudit(Balance balance) {
        return BalanceAudit.builder()
                .accountNumber(balance.getAccount().getPaymentNumber())
                .version(balance.getVersion())
                .authorizationBalance(balance.getAuthorisationBalance())
                .actualBalance(balance.getActualBalance())
                .build();
    }
}
