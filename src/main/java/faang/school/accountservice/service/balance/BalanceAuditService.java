package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceAuditResponseDto;

public interface BalanceAuditService {
    BalanceAuditResponseDto getBalanceAudit(Long id);
}
