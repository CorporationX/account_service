package faang.school.accountservice.service.balance_audit;

import faang.school.accountservice.dto.balance.BalanceUpdateDto;
import faang.school.accountservice.dto.balance_audit.BalanceAuditDto;
import faang.school.accountservice.dto.balance_audit.BalanceAuditFilterDto;

import java.util.List;

public interface BalanceAuditService {

    void createNewAudit(BalanceUpdateDto balanceUpdateDto);

    List<BalanceAuditDto> findByAccountId(long accountId, BalanceAuditFilterDto balanceAuditFilterDto);
}
