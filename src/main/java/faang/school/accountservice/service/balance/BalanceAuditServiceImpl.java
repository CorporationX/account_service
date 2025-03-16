package faang.school.accountservice.service.balance;

import faang.school.accountservice.dto.BalanceAuditResponseDto;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceAuditServiceImpl implements BalanceAuditService {
    private BalanceAuditMapper balanceAuditMapper;
    private BalanceAuditRepository balanceAuditRepository;

    @Override
    public BalanceAuditResponseDto getBalanceAudit(Long id) {
        return balanceAuditMapper.toBalanceAuditResponseDto(balanceAuditRepository.findByBalanceId(id));
    }

    @Override
    public BalanceAuditResponseDto getBalanceAuditForBalance(Balance balance) {
        BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAuditFromBalance(balance);
        return balanceAuditMapper.toBalanceAuditResponseDto(balanceAudit);
    }
}
