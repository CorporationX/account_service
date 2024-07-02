package faang.school.accountservice.service.balance_audit.filter;

import faang.school.accountservice.dto.balance_audit.BalanceAuditFilterDto;
import faang.school.accountservice.model.BalanceAudit;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class BalanceAuditStartDateFilter implements BalanceAuditFilter {

    @Override
    public boolean isAcceptable(BalanceAuditFilterDto balanceAuditFilterDto) {
        return balanceAuditFilterDto.getStartDate() != null;
    }

    @Override
    public Stream<BalanceAudit> accept(Stream<BalanceAudit> balanceAuditFilterStream, BalanceAuditFilterDto balanceAuditFilterDto) {
        return balanceAuditFilterStream
                .filter(balanceAuditDto -> balanceAuditDto.getCreatedAt().isAfter(balanceAuditFilterDto.getStartDate()));
    }
}
