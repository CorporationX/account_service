package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.model.dto.PaymentMessageDto;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.model.AccountBalance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.repository.AccountBalanceRepository;
import faang.school.accountservice.repository.BalanceAuditRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountBalanceRepository balanceRepository;
    private final BalanceAuditRepository auditRepository;
    private final UserContext userContext;

    @Override
    @Transactional
    public void processAuthorization(PaymentMessageDto message) {
        AccountBalance balance = balanceRepository.getByIdOrThrow(message.getToAccountId());
        BigDecimal oldAvailable = balance.getAvailable();

        balance.setAvailable(oldAvailable.add(message.getAmount()));
        balanceRepository.save(balance);

        auditRepository.save(BalanceAudit.builder()
                .accountId(balance.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(message.getAmount())
                .currency(balance.getCurrency())
                .oldBalance(oldAvailable)
                .newBalance(balance.getAvailable())
                .eventType("AUTHORIZATION")
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public void processCancel(PaymentMessageDto message) {
        AccountBalance balance = balanceRepository.getByIdOrThrow(message.getToAccountId());
        BigDecimal oldAvailable = balance.getAvailable();

        balance.setAvailable(oldAvailable.subtract(message.getAmount()));
        balanceRepository.save(balance);

        auditRepository.save(BalanceAudit.builder()
                .accountId(balance.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(message.getAmount().negate())
                .currency(balance.getCurrency())
                .oldBalance(oldAvailable)
                .newBalance(balance.getAvailable())
                .eventType("CANCEL")
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public void processClearing(PaymentMessageDto message) {
        AccountBalance balance = balanceRepository.getByIdOrThrow(message.getToAccountId());

        auditRepository.save(BalanceAudit.builder()
                .accountId(balance.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(BigDecimal.ZERO)
                .currency(balance.getCurrency())
                .oldBalance(balance.getAvailable())
                .newBalance(balance.getAvailable())
                .eventType("CLEARING")
                .createdAt(LocalDateTime.now())
                .build());
    }

    @Override
    @Transactional
    public AccountBalance getBalance(Long accountId) {
        long userId = userContext.getUserId();
        return balanceRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found for this user"));
    }

    @Override
    @Transactional
    public List<BalanceAudit> getAudit(Long accountId) {
        long userId = userContext.getUserId();
        return auditRepository.getByAccountIdOrThrow(accountId, userId);
    }
}