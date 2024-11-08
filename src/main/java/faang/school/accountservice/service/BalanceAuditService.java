package faang.school.accountservice.service;

import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.BalanceAudit;
import faang.school.accountservice.model.entity.Request;

public interface BalanceAuditService {
    BalanceAudit save(Balance balance);
    BalanceAudit save(Balance balance, Request request);
}
