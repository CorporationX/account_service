package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceAuditResponseDto;
import faang.school.accountservice.entity.Balance;

public interface BalanceAuditService {
    BalanceAuditResponseDto getBalanceAudit(Long id);

    BalanceAuditResponseDto getBalanceAuditForBalance(Balance balance);
}
