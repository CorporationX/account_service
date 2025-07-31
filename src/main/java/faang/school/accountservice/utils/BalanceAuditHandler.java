package faang.school.accountservice.utils;

import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.exception.BalanceAuditHandlerException;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.randomizer.OperationRandomizer;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BalanceAuditHandler {
    private final BalanceAuditMapper balanceAuditMapper;
    private final BalanceAuditRepository balanceAuditRepository;
    private final OperationRandomizer operationRandomizer;

    public void balanceAuditHandle(Balance balance) {
        try {
            log.debug("Start handle balance audit for balance with id: {}", balance.getId());
            BalanceAudit balanceAudit = balanceAuditMapper.toBalanceAudit(balance);
            balanceAudit.setId(null);
            balanceAudit.setOperationId(operationRandomizer.randomOperationId());
            balanceAudit.setVersion(balance.getVersion());
            balanceAuditRepository.save(balanceAudit);
            log.debug("Handle balance audit for balance with id: {} was finished", balance.getId());
        } catch (RuntimeException e) {
            String errorMessage = "Handle balance audit for Balance with id: %d was failed".formatted(balance.getId());
            log.error(errorMessage, e);
            throw new BalanceAuditHandlerException(errorMessage);
        }
    }
}