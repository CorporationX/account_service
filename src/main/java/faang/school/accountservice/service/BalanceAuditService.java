package faang.school.accountservice.service;

import faang.school.accountservice.model.dto.audit.BalanceAuditDto;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.enums.OperationType;

import java.util.List;

public interface BalanceAuditService {

    void saveBalanceAudit(Balance balance, OperationType type);

    BalanceAuditDto getBalanceAudit(long balanceId);

    List<BalanceAuditDto> getAllBalanceAudit();
}
