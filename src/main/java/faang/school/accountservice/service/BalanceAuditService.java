package faang.school.accountservice.service;

import faang.school.accountservice.mapper.BalanceMapper;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Сервис для аудита изменений баланса.
 * Сохраняет исторические данные об изменениях баланса для последующего анализа и отслеживания операций.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceAuditService {
    private final BalanceMapper balanceMapper;
    private final BalanceAuditRepository balanceAuditRepository;

    @Transactional
    public void setAudit(Balance balance, UUID operationId) {
        BalanceAudit audit = balanceMapper.toBalanceAudit(balance, operationId);
        balanceAuditRepository.save(audit);
    }
}
