package faang.school.accountservice.service;

import faang.school.accountservice.config.context.UserContext;
import faang.school.accountservice.enums.PaymentStages;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.kafka.AccountProducer;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис работы с балансами.
 * <p>
 * Методы реализуют логику авторизации, отмены и клиринга платежей.
 * Аудит всех изменений сохраняется в BalanceAudit.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountBalanceServiceImpl implements AccountBalanceService {

    private final AccountBalanceRepository balanceRepository;
    private final BalanceAuditRepository auditRepository;
    private final AccountServiceMapper mapper;
    private final UserContext userContext;
    private final AccountProducer accountProducer;

    @Override
    @Transactional
    public void processAuthorization(PaymentMessageDto message) {
        try {
            AccountBalance balance = balanceRepository.getByIdOrThrow(message.getFromAccountId());
            if (balance.getAvailable().compareTo(message.getAmount()) < 0) {
                handleFailure(message, "Недостаточно средств для авторизации");
                return;
            }

            updateBalance(balance, message.getAmount(), PaymentStages.AUTHORIZED, message);
            log.info("Авторизация прошла для аккаунта {} на сумму {} {}",
                    balance.getId(), message.getAmount(), balance.getCurrency());

        } catch (Exception ex) {
            log.error("Ошибка авторизации платежа: {}", message, ex);
            handleFailure(message, ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void processCancel(PaymentMessageDto message) {
        try {
            AccountBalance balance = balanceRepository.getByIdOrThrow(message.getFromAccountId());
            updateBalance(balance, message.getAmount(), PaymentStages.CANCELED, message);
            log.info("Отмена прошла для аккаунта {} на сумму {} {}",
                    balance.getId(), message.getAmount(), balance.getCurrency());

        } catch (Exception ex) {
            log.error("Ошибка отмены платежа: {}", message, ex);
            handleFailure(message, ex.getMessage());
        }
    }

    @Override
    @Transactional
    public void processClearing(PaymentMessageDto message) {
        try {
            AccountBalance payer = balanceRepository.getByIdOrThrow(message.getFromAccountId());
            AccountBalance payee = balanceRepository.getByIdOrThrow(message.getToAccountId());
            BigDecimal amount = message.getAmount();

            if (payer.getReserved().compareTo(amount) < 0) {
                handleFailure(message, "Недостаточно зарезервированных средств для клиринга");
                return;
            }

            updateBalance(payer, amount.negate(), PaymentStages.CLEARED, message); // снимаем резерв
            updateBalance(payee, amount, PaymentStages.CLEARED, message); // добавляем получателю
            log.info("Клиринг проведён: {} → {} сумма {} {}",
                    payer.getId(), payee.getId(), amount, payer.getCurrency());

        } catch (Exception ex) {
            log.error("Ошибка клиринга платежа: {}", message, ex);
            handleFailure(message, ex.getMessage());
        }
    }

    private void updateBalance(AccountBalance balance, BigDecimal change, PaymentStages stage, PaymentMessageDto message) {
        BigDecimal oldAvailable = balance.getAvailable();
        BigDecimal oldReserved = balance.getReserved();

        switch (stage) {
            case AUTHORIZED -> {
                balance.setAvailable(oldAvailable.subtract(change));
                balance.setReserved(oldReserved.add(change));
            }
            case CANCELED -> {
                balance.setAvailable(oldAvailable.add(change));
                balance.setReserved(oldReserved.subtract(change));
            }
            case CLEARED -> {
                // для клиринга change может быть отрицательным для payer
                if (change.signum() < 0) {
                    balance.setReserved(oldReserved.add(change)); // change отрицательный
                } else {
                    balance.setAvailable(oldAvailable.add(change));
                }
            }
            default -> throw new IllegalArgumentException("Неизвестный этап платежа: " + stage);
        }

        balanceRepository.save(balance);
        createAudit(balance, change, stage, message);
    }

    private void createAudit(AccountBalance balance, BigDecimal change, PaymentStages stage, PaymentMessageDto message) {
        BigDecimal oldBalance = (stage == PaymentStages.AUTHORIZED) ? balance.getAvailable().add(change)
                : (stage == PaymentStages.CANCELED) ? balance.getAvailable().subtract(change)
                : balance.getAvailable().subtract(change.signum() < 0 ? change.negate() : BigDecimal.ZERO);

        BalanceAudit audit = BalanceAudit.builder()
                .accountId(balance.getId())
                .requestId(message.getIdempotencyToken())
                .changeAmount(change)
                .currency(balance.getCurrency())
                .oldBalance(oldBalance)
                .newBalance(balance.getAvailable())
                .eventType(stage)
                .createdAt(LocalDateTime.now())
                .build();
        auditRepository.save(audit);
    }

    private void handleFailure(PaymentMessageDto message, String reason) {
        try {
            AccountBalance balance = balanceRepository.findById(message.getFromAccountId()).orElse(null);
            BigDecimal oldBalance = (balance != null) ? balance.getAvailable().add(balance.getReserved()) : BigDecimal.ZERO;
            BigDecimal newBalance = oldBalance;
            String currency = (balance != null) ? balance.getCurrency() : "USD";

            BalanceAudit audit = BalanceAudit.builder()
                    .accountId(message.getFromAccountId())
                    .requestId(message.getIdempotencyToken())
                    .changeAmount(message.getAmount().negate())
                    .currency(currency)
                    .oldBalance(oldBalance)
                    .newBalance(newBalance)
                    .eventType(PaymentStages.FAILED)
                    .comment(reason)
                    .build();
            auditRepository.save(audit);

            accountProducer.sendFailed(message);
            log.info("Отправлено сообщение FAILED для платежа {}: {}", message.getIdempotencyToken(), reason);
        } catch (Exception ex) {
            log.error("Ошибка при обработке FAILED для платежа {}: {}, причина: {}",
                    message.getIdempotencyToken(), reason, ex.getMessage(), ex);
        }
    }

    @Override
    @Transactional
    public AccountBalanceDto getBalance(Long accountId) {
        long userId = userContext.getUserId();
        AccountBalance balance = balanceRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Аккаунт не найден для пользователя"));
        log.info("Баланс запрошен для аккаунта {}: {}", accountId, balance.getAvailable());
        return mapper.toDto(balance);
    }

    @Override
    @Transactional
    public List<BalanceAuditDto> getAudit(Long accountId) {
        long userId = userContext.getUserId();
        List<BalanceAudit> audits = auditRepository.getByAccountIdOrThrow(accountId, userId);
        log.info("Получен аудит для аккаунта {}, количество записей: {}", accountId, audits.size());
        return mapper.toDtoList(audits);
    }
}