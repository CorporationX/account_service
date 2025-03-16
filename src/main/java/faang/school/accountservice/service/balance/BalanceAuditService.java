package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceAuditResponseDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;

public interface BalanceAuditService {
    BalanceAuditResponseDto getBalanceAudit (Long id);
    BalanceAuditResponseDto getBalanceAuditForBalance (Long id);
    BalanceAuditResponseDto addEntryToBalanceAudit(Long balanceId);
}
