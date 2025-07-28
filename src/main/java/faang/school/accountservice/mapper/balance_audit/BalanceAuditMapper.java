package faang.school.accountservice.mapper.balance_audit;

import faang.school.accountservice.dto.balance_audit_dto.BalanceAuditDto;
import faang.school.accountservice.entity.BalanceAudit;

public class BalanceAuditMapper {

    public static BalanceAuditDto toBalanceAudit(BalanceAudit balanceAudit){
        return new BalanceAuditDto(
                balanceAudit.getId(),
                balanceAudit.getBalanceVersion(),
                balanceAudit.getBalanceChangeId(),
                balanceAudit.getCreatedAt()
        );
    }
}
