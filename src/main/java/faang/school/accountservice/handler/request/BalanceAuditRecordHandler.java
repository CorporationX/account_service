package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceAuditRecordHandler implements RequestTaskHandler<BalanceDto> {
    private final BalanceAuditMapper auditMapper;
    private final BalanceAuditRepository balanceAuditRepository;

    @Override
    public void execute(BalanceDto balanceDto) {
        balanceAuditRepository.save(auditMapper.toEntity(balanceDto));
    }

    @Override
    public RequestHandlerType getHandlerId() {
        return RequestHandlerType.AUDIT_RECORD_HANDLER;
    }
}
