package faang.school.accountservice.service;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;

import java.math.BigDecimal;

public interface BalanceAuditService {

    BalanceAudit recordBalanceChange(BigDecimal authBalance, BigDecimal factBalance, Balance balance);
}
