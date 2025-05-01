package faang.school.accountservice.service;

import faang.school.accountservice.model.AccountOperation;
import faang.school.accountservice.model.Balance;
import faang.school.accountservice.repository.BalanceRepository;
import faang.school.accountservice.validation.BalanceValidator;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceValidator balanceValidator;
    private final BalanceRepository balanceRepository;
    private final BalanceAuditService balanceAuditService;

    @Transactional
    public void reserveFounds(@NotNull AccountOperation operation) {
        UUID senderAccountId = operation.getSenderAccountId();
        Balance senderBalance = balanceRepository.findByAccountId(senderAccountId)
                .orElseThrow(() -> new IllegalStateException("The sender account does not exist."));

        balanceValidator.authValidation(senderBalance, operation);

        BigDecimal clearBalance = senderBalance.getClearBalance();
        BigDecimal amount = operation.getAmount();
        BigDecimal newClearBalance = clearBalance.subtract(amount);
        senderBalance.setClearBalance(newClearBalance);

        BigDecimal authBalance = senderBalance.getAuthBalance();
        BigDecimal newAuthBalance = authBalance.add(amount);
        senderBalance.setAuthBalance(newAuthBalance);

        senderBalance.setUpdatedAt(LocalDateTime.now());
        balanceRepository.save(senderBalance);

        balanceAuditService.setAudit(senderBalance, operation.getPaymentOperationId());
    }

    @Transactional
    public void clearBalance(@NotNull AccountOperation operation) {
        UUID senderAccountId = operation.getSenderAccountId();
        UUID recipientAccountId = operation.getRecipientAccountId();
        BigDecimal amount = operation.getAmount();

        Balance senderBalance = balanceRepository.findByAccountId(senderAccountId)
                .orElseThrow(() -> new IllegalStateException("The sender account does not exist."));

        Balance recipientBalance = balanceRepository.findByAccountId(recipientAccountId)
                .orElseThrow(() -> new IllegalStateException("The recipient account does not exist."));

        balanceValidator.clearValidation(senderBalance, recipientBalance, amount);

        BigDecimal newSenderAuthBalance = senderBalance.getAuthBalance().subtract(amount);
        senderBalance.setAuthBalance(newSenderAuthBalance);
        senderBalance.setUpdatedAt(LocalDateTime.now());
        balanceRepository.save(senderBalance);

        BigDecimal newRecipientClearBalance = recipientBalance.getClearBalance().add(amount);
        recipientBalance.setClearBalance(newRecipientClearBalance);
        recipientBalance.setUpdatedAt(LocalDateTime.now());
        balanceRepository.save(recipientBalance);

        balanceAuditService.setAudit(senderBalance, operation.getId());
        balanceAuditService.setAudit(recipientBalance, operation.getId());
    }

    public void cancelBalance(@NotNull AccountOperation operation) {
        UUID senderAccountId = operation.getSenderAccountId();
        BigDecimal amount = operation.getAmount();

        Balance senderBalance = balanceRepository.findByAccountId(senderAccountId)
                .orElseThrow(() -> new IllegalStateException("The sender account does not exist."));

        balanceValidator.cancelValidation(senderBalance, amount);

        BigDecimal newAuthBalance = senderBalance.getAuthBalance().subtract(amount);
        BigDecimal newClearBalance = senderBalance.getClearBalance().add(amount);

        senderBalance.setAuthBalance(newAuthBalance);
        senderBalance.setClearBalance(newClearBalance);
        senderBalance.setUpdatedAt(LocalDateTime.now());

        balanceRepository.save(senderBalance);

        balanceAuditService.setAudit(senderBalance, operation.getId());
    }
}
