package faang.school.accountservice.service.balance;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.entity.Balance;
import faang.school.accountservice.entity.BalanceAudit;
import faang.school.accountservice.entity.User;
import faang.school.accountservice.enums.AuditEventType;
import faang.school.accountservice.enums.BalanceAuditStatus;
import faang.school.accountservice.exception.BalanceAuditException;
import faang.school.accountservice.repository.BalanceAuditRepository;
import faang.school.accountservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class AsyncAuditService {
    private final BalanceAuditRepository repository;
    private final UserContext userContext;
    private final UserService userService;

    @Async("auditBalanceExecutor")
    public void auditBalanceCreation(Balance balance) {
        User initiator = getInitiator();

        BalanceAudit newBalanceAudit = BalanceAudit.builder()
                .initiator(initiator)
                .eventType(AuditEventType.BALANCE_CREATION)
                .currentAuthAmount(balance.getAuthBalance())
                .previousAuthAmount(null)
                .currentFactAmount(balance.getCurrentBalance())
                .previousFactAmount(null)
                .auditStatus(BalanceAuditStatus.SUCCEED)
                .build();

        saveBalanceAudit(newBalanceAudit);
    }

    @Async("auditBalanceExecutor")
    public void auditBalanceChange(Balance prevBalance, Balance actualBalance, AuditEventType eventType) {
        User initiator = getInitiator();

        BalanceAudit newBalanceAudit = BalanceAudit.builder()
                .initiator(initiator)
                .eventType(eventType)
                .currentAuthAmount(actualBalance.getAuthBalance())
                .previousAuthAmount(prevBalance.getAuthBalance())
                .currentFactAmount(actualBalance.getCurrentBalance())
                .previousFactAmount(prevBalance.getCurrentBalance())
                .auditStatus(BalanceAuditStatus.SUCCEED)
                .build();

        saveBalanceAudit(newBalanceAudit);
    }

    @Transactional(readOnly = true)
    public BalanceAudit getBalanceAudit(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BalanceAuditException(String.format("BalanceAudit with id %d not found", id)));
    }

    @Async("auditBalanceExecutor")
    public void saveFailedBalanceAudit(AuditEventType eventType) {
        User initiator = getInitiator();

        BalanceAudit failedBalanceAudit = BalanceAudit.builder()
                .initiator(initiator)
                .eventType(eventType)
                .currentAuthAmount(BigDecimal.ZERO)
                .currentFactAmount(BigDecimal.ZERO)
                .auditStatus(BalanceAuditStatus.FAILED)
                .build();

        saveBalanceAudit(failedBalanceAudit);
    }

    @Transactional
    public void saveBalanceAudit(BalanceAudit audit) {
        repository.save(audit);
    }

    private User getInitiator() {
        Long userId = userContext.getUserId();
        return userService.getUser(userId);
    }
}
