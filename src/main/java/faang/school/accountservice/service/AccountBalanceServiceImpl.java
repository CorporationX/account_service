package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.enums.PaymentStages;
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

/**
 * Реализация сервиса работы с балансами для DMS:
 * - Authorization: резервирует средства у плательщика (available -> reserved)
 * - Cancel: возвращает резерв (reserved -> available)
 * - Clearing: переводит резерв с плательщика получателю (reserved(payer) -> available(payee))
 */
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
        AccountBalance balance = balanceRepository.getByIdOrThrow(message.getFromAccountId());
        BigDecimal oldAvailable = balance.getAvailable();
        BigDecimal oldReserved = balance.getReserved();

        if (oldAvailable.compareTo(message.getAmount()) < 0) {
            throw new IllegalArgumentException("Недостаточно средств для авторизации");
        }

        balance.setAvailable(oldAvailable.subtract(message.getAmount()));
        balance.setReserved(oldReserved.add(message.getAmount()));
        balanceRepository.save(balance);

        BalanceAudit audit = BalanceAudit.builder()
                .accountId(balance.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(message.getAmount())
                .currency(balance.getCurrency())
                .oldBalance(oldAvailable)
                .newBalance(balance.getAvailable())
                .eventType(PaymentStages.AUTHORIZED)
                .createdAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);
    }

    @Override
    @Transactional
    public void processCancel(PaymentMessageDto message) {
        AccountBalance balance = balanceRepository.getByIdOrThrow(message.getFromAccountId());
        BigDecimal oldAvailable = balance.getAvailable();
        BigDecimal oldReserved = balance.getReserved();

        balance.setAvailable(oldAvailable.add(message.getAmount()));
        balance.setReserved(oldReserved.subtract(message.getAmount()));
        balanceRepository.save(balance);

        BalanceAudit audit = BalanceAudit.builder()
                .accountId(balance.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(message.getAmount().negate())
                .currency(balance.getCurrency())
                .oldBalance(oldAvailable)
                .newBalance(balance.getAvailable())
                .eventType(PaymentStages.CANCELED)
                .createdAt(LocalDateTime.now())
                .build();

        auditRepository.save(audit);
    }

    @Override
    @Transactional
    public void processClearing(PaymentMessageDto message) {
        AccountBalance payer = balanceRepository.getByIdOrThrow(message.getFromAccountId());
        AccountBalance payee = balanceRepository.getByIdOrThrow(message.getToAccountId());

        BigDecimal amount = message.getAmount();

        BigDecimal oldPayerReserved = payer.getReserved();
        BigDecimal oldPayeeAvailable = payee.getAvailable();

        payer.setReserved(oldPayerReserved.subtract(amount));
        balanceRepository.save(payer);

        payee.setAvailable(oldPayeeAvailable.add(amount));
        balanceRepository.save(payee);

        BalanceAudit auditPayer = BalanceAudit.builder()
                .accountId(payer.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(amount.negate())
                .currency(payer.getCurrency())
                .oldBalance(oldPayerReserved)
                .newBalance(payer.getReserved())
                .eventType(PaymentStages.CLEARED)
                .createdAt(LocalDateTime.now())
                .build();
        auditRepository.save(auditPayer);

        BalanceAudit auditPayee = BalanceAudit.builder()
                .accountId(payee.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(amount)
                .currency(payee.getCurrency())
                .oldBalance(oldPayeeAvailable)
                .newBalance(payee.getAvailable())
                .eventType(PaymentStages.CLEARED)
                .createdAt(LocalDateTime.now())
                .build();
        auditRepository.save(auditPayee);
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