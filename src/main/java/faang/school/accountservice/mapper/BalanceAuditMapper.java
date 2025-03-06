package faang.school.accountservice.mapper;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;

public class BalanceAuditMapper {

    public BalanceAudit convertBalanceToBalanceAudit(Balance balance) {
        return BalanceAudit.builder()
                .balance(balance)
                .accountNumber(balance.getAccount().getAccountNumber())
                .balanceVersion(balance.getVersion())
                .authAmount(balance.getAuthBalance())
                .factAmount(balance.getCurrentBalance())
                .build();
    }
}
//TODO эндпоинт для получения аудита, полная информация
