package faang.school.accountservice.service.balance_audit.filter.service;

import faang.school.accountservice.dto.balance_audit.BalanceAuditFilterDto;
import faang.school.accountservice.model.BalanceAudit;

import java.util.stream.Stream;

public interface BalanceAuditFilterService {

    Stream<BalanceAudit> acceptAll(Stream<BalanceAudit> balanceAuditStream, BalanceAuditFilterDto balanceAuditFilterDto);
}
