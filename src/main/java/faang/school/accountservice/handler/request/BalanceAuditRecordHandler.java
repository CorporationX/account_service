package faang.school.accountservice.handler.request;

import faang.school.accountservice.dto.AccountDto;
import faang.school.accountservice.enums.RequestHandlerType;
import faang.school.accountservice.mapper.AccountMapper;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceAuditRecordHandler implements RequestTaskHandler<AccountDto> {
    private final BalanceAuditMapper auditMapper;
    private final BalanceAuditRepository balanceAuditRepository;
    private final AccountMapper accountMapper;

    @Override
    public void execute(AccountDto accountDto) {
        balanceAuditRepository.save(auditMapper.toEntity(accountMapper.toEntity(accountDto)));
    }

    @Override
    public RequestHandlerType getHandlerId() {
        return RequestHandlerType.AUDIT_RECORD_HANDLER;
    }
}
