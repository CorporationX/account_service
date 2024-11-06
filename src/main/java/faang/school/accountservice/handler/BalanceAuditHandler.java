package faang.school.accountservice.handler;

import faang.school.accountservice.dto.BalanceDto;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceAuditHandler implements RequestTaskHandler<BalanceDto> {
    private final BalanceAuditMapper auditMapper;
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceMapper balanceMapper;

    @Override
    public void execute(BalanceDto balance) {
        balanceAuditRepository.save(auditMapper.toEntity(balanceMapper.toEntity(balance)));
    }

    @Override
    public Long getHandlerId() {
        return 4L;
    }
}
