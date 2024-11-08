package faang.school.accountservice.service.impl;

import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.entity.Balance;
import faang.school.accountservice.model.entity.BalanceAudit;
import faang.school.accountservice.model.entity.Request;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.BalanceAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BalanceAuditServiceImpl implements BalanceAuditService {
    private final BalanceAuditRepository balanceAuditRepository;
    private final BalanceAuditMapper balanceAuditMapper;

    @Override
    public BalanceAudit save(Balance balance) {
        return save(balance, null);
    }

    @Override
    public BalanceAudit save(Balance balance, Request request) {
        BalanceAudit balanceAuditEntity = balanceAuditMapper.toAuditEntity(balance);
        balanceAuditEntity.setRequest(request);
        return balanceAuditRepository.save(balanceAuditEntity);
    }
}
