package faang.school.accountservice.service.balance;

import faang.school.accountservice.exception.EntityNotFound;
import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.model.BalanceDMS;
import faang.school.accountservice.repository.BalanceDMSRepository;
import faang.school.accountservice.validation.BalanceDMSValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сервис для управления балансом счетов.
 * Обеспечивает резервирование, списание и отмену операций с балансом,
 * а также валидацию операций перед их выполнением.
 */
@Service
@RequiredArgsConstructor
public class BalanceDMSService {
    private final BalanceDMSValidator balanceDMSValidator;
    private final BalanceDMSRepository balanceDMSRepository;
    private final BalanceAuditService balanceAuditService;

    @Transactional
    public void reserveFounds(@NotNull AccountOperation operation) {
        UUID senderAccountId = operation.getSenderAccountId();
        BalanceDMS senderBalanceDMS = balanceDMSRepository.findByAccountId(senderAccountId)
                .orElseThrow(() -> new EntityNotFound("The sender account does not exist."));

        balanceDMSValidator.authValidation(senderBalanceDMS, operation);

        BigDecimal clearBalance = senderBalanceDMS.getClearBalance();
        BigDecimal amount = operation.getAmount();
        BigDecimal newClearBalance = clearBalance.subtract(amount);
        senderBalanceDMS.setClearBalance(newClearBalance);

        BigDecimal authBalance = senderBalanceDMS.getAuthBalance();
        BigDecimal newAuthBalance = authBalance.add(amount);
        senderBalanceDMS.setAuthBalance(newAuthBalance);

        senderBalanceDMS.setUpdatedAt(LocalDateTime.now());
        balanceDMSRepository.save(senderBalanceDMS);

        balanceAuditService.setAudit(senderBalanceDMS, operation.getPaymentOperationId());
    }

    @Transactional
    public void clearBalance(@NotNull AccountOperation operation) {
        UUID senderAccountId = operation.getSenderAccountId();
        UUID recipientAccountId = operation.getRecipientAccountId();
        BigDecimal amount = operation.getAmount();

        BalanceDMS senderBalanceDMS = balanceDMSRepository.findByAccountId(senderAccountId)
                .orElseThrow(() -> new EntityNotFound("The sender account does not exist."));

        BalanceDMS recipientBalanceDMS = balanceDMSRepository.findByAccountId(recipientAccountId)
                .orElseThrow(() -> new EntityNotFound("The recipient account does not exist."));

        balanceDMSValidator.clearValidation(senderBalanceDMS, recipientBalanceDMS, amount);

        BigDecimal newSenderAuthBalance = senderBalanceDMS.getAuthBalance().subtract(amount);
        senderBalanceDMS.setAuthBalance(newSenderAuthBalance);
        senderBalanceDMS.setUpdatedAt(LocalDateTime.now());
        balanceDMSRepository.save(senderBalanceDMS);

        BigDecimal newRecipientClearBalance = recipientBalanceDMS.getClearBalance().add(amount);
        recipientBalanceDMS.setClearBalance(newRecipientClearBalance);
        recipientBalanceDMS.setUpdatedAt(LocalDateTime.now());
        balanceDMSRepository.save(recipientBalanceDMS);

        balanceAuditService.setAudit(senderBalanceDMS, operation.getId());
        balanceAuditService.setAudit(recipientBalanceDMS, operation.getId());
    }

    @Transactional
    public void cancelBalance(@NotNull AccountOperation operation) {
        UUID senderAccountId = operation.getSenderAccountId();
        BigDecimal amount = operation.getAmount();

        BalanceDMS senderBalanceDMS = balanceDMSRepository.findByAccountId(senderAccountId)
                .orElseThrow(() -> new EntityNotFound("The sender account does not exist."));

        balanceDMSValidator.cancelValidation(senderBalanceDMS, amount);

        BigDecimal newAuthBalance = senderBalanceDMS.getAuthBalance().subtract(amount);
        BigDecimal newClearBalance = senderBalanceDMS.getClearBalance().add(amount);

        senderBalanceDMS.setAuthBalance(newAuthBalance);
        senderBalanceDMS.setClearBalance(newClearBalance);
        senderBalanceDMS.setUpdatedAt(LocalDateTime.now());

        balanceDMSRepository.save(senderBalanceDMS);

        balanceAuditService.setAudit(senderBalanceDMS, operation.getId());
    }
}
