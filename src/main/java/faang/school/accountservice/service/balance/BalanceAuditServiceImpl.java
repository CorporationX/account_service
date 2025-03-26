package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceAuditResponseDto;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceAuditServiceImpl implements BalanceAuditService {
    private BalanceAuditMapper balanceAuditMapper;
    private BalanceAuditRepository balanceAuditRepository;

    @Override
    public BalanceAuditResponseDto getBalanceAudit(Long id) {
        return balanceAuditMapper.toBalanceAuditResponseDto(balanceAuditRepository.findByBalanceId(id));
    }
}
