package faang.school.accountservice.listener;

import faang.school.accountservice.event.BalanceChangeEvent;
import faang.school.accountservice.mapper.BalanceAuditMapper;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.BalanceAuditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BalanceAuditListener {
    private final BalanceAuditMapper balanceAuditMapper;
    private final BalanceAuditRepository balanceAuditRepository;

    @EventListener
    @Transactional
    public void onBalanceChanged(BalanceChangeEvent event) {
        try {
            validateEvent(event);
            log.debug("Processing balance change event for account: {}", event.balance().getAccount().getId());

            BalanceAudit audit = balanceAuditMapper.toAudit(event.balance(), event.operationId());
            BalanceAudit savedAudit = balanceAuditRepository.save(audit);

            log.info("Successfully saved audit record with ID: {} for account: {}",
                    savedAudit.getId(), event.balance().getAccount().getId());

        } catch (IllegalArgumentException e) {
            log.warn("Validation failed for balance change event: {}", event, e);
            throw e;

        } catch (Exception e) {
            log.error("Failed to save audit record for event: {}", event, e);
            throw e;
        }
    }

    private void validateEvent(BalanceChangeEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        if (event.balance() == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        if (event.operationId() == null) {
            throw new IllegalArgumentException("Operation ID cannot be null");
        }
    }
}
