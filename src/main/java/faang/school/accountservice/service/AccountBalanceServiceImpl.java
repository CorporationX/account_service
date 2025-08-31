package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.mapper.AccountServiceMapper;
import faang.school.accountservice.model.AccountBalance;
import faang.school.accountservice.model.BalanceAudit;
import faang.school.accountservice.model.dto.AccountBalanceDto;
import faang.school.accountservice.model.dto.BalanceAuditDto;
import faang.school.accountservice.model.dto.PaymentMessageDto;
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
public class AccountBalanceServiceImpl implements AccountBalanceService {

    private final AccountBalanceRepository balanceRepository;
    private final BalanceAuditRepository auditRepository;
    private final AccountServiceMapper mapper;
    private final UserContext userContext;

    @Override
    @Transactional
    public void processAuthorization(PaymentMessageDto message) {
        AccountBalance balance = balanceRepository.getByIdOrThrow(message.getToAccountId());
        BigDecimal oldAvailable = balance.getAvailable();

        balance.setAvailable(oldAvailable.add(message.getAmount()));
        balanceRepository.save(balance);

        BalanceAudit audit = BalanceAudit.builder()
                .accountId(balance.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(message.getAmount())
                .currency(balance.getCurrency())
                .oldBalance(oldAvailable)
                .newBalance(balance.getAvailable())
                .eventType("AUTHORIZATION")
                .createdAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);
    }

    @Override
    @Transactional
    public void processCancel(PaymentMessageDto message) {
        AccountBalance balance = balanceRepository.getByIdOrThrow(message.getToAccountId());
        BigDecimal oldAvailable = balance.getAvailable();

        balance.setAvailable(oldAvailable.subtract(message.getAmount()));
        balanceRepository.save(balance);

        BalanceAudit audit = BalanceAudit.builder()
                .accountId(balance.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(message.getAmount().negate())
                .currency(balance.getCurrency())
                .oldBalance(oldAvailable)
                .newBalance(balance.getAvailable())
                .eventType("CANCEL")
                .createdAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);
    }

    @Override
    @Transactional
    public void processClearing(PaymentMessageDto message) {
        AccountBalance balance = balanceRepository.getByIdOrThrow(message.getToAccountId());

        BalanceAudit audit = BalanceAudit.builder()
                .accountId(balance.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(BigDecimal.ZERO)
                .currency(balance.getCurrency())
                .oldBalance(balance.getAvailable())
                .newBalance(balance.getAvailable())
                .eventType("CLEARING")
                .createdAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);
    }

    @Override
    @Transactional
    public AccountBalanceDto getBalance(Long accountId) {
        long userId = userContext.getUserId();
        AccountBalance balance = balanceRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found for this user"));
        return mapper.toDto(balance);
    }

    @Override
    @Transactional
    public List<BalanceAuditDto> getAudit(Long accountId) {
        long userId = userContext.getUserId();
        List<BalanceAudit> audits = auditRepository.getByAccountIdOrThrow(accountId, userId);
        return mapper.toDtoList(audits);
    }
}