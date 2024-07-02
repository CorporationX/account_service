package faang.school.accountservice.service.balance_audit.filter;

import faang.school.accountservice.dto.balance_audit.BalanceAuditFilterDto;
import faang.school.accountservice.model.BalanceAudit;

import java.util.stream.Stream;

public interface BalanceAuditFilter {

    boolean isAcceptable(BalanceAuditFilterDto balanceAuditFilterDto);

    Stream<BalanceAudit> accept(Stream<BalanceAudit> balanceAuditFilterStream, BalanceAuditFilterDto balanceAuditFilterDto);
}
