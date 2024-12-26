package faang.school.accountservice.message;

import faang.school.accountservice.dto.AuthorizationEvent;
import faang.school.accountservice.enums.BalanceStatus;
import faang.school.accountservice.exception.AccountNotFoundException;
import faang.school.accountservice.exception.BalanceLimitException;
import faang.school.accountservice.model.Account;
import faang.school.accountservice.repository.AccountRepository;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorizationEventHandler {
    private final AccountRepository accountRepository;
    private final BalanceService balanceService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void handle(AuthorizationEvent event) {
        // Проверяем, что event содержит senderId и recipientId
        if (event.getSenderId() == null || event.getRecipientId() == null) {
            throw new IllegalArgumentException("SenderId and RecipientId cannot be null");
        }

        // Получаем аккаунт отправителя
        Account senderAccount = accountRepository.findById(event.getSenderId())
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found"));

        // Получаем аккаунт получателя
        Account recipientAccount = accountRepository.findById(event.getRecipientId())
                .orElseThrow(() -> new AccountNotFoundException("Recipient account not found"));

        // Проверяем, достаточно ли средств у отправителя
        if (!hasSufficientBalance(senderAccount.getBalance().getActualBalance(), event.getAmount())
                || senderAccount.getBalance().getBalanceStatus() == BalanceStatus.CANCELLED)
        {
            log.warn("Payment cancelled: insufficient balance for sender account {}. Required: {}, available: {}",
                    senderAccount.getId(), event.getAmount(), senderAccount.getBalance().getActualBalance());
            kafkaTemplate.send("cancel-payment-topic", event); // Отправляем событие отмены
            throw new BalanceLimitException("Balance is not enough or cancelled");
        }

        // Выполняем авторизацию платежа
        balanceService.authorizePayment(senderAccount, event.getAmount());
        log.info("Payment authorized for sender account {}", senderAccount.getId());

        // Отправляем событие успешной авторизации
        kafkaTemplate.send("successful-payment-auth-topic", event);
        log.info("Auth event sent for recipient account {}", recipientAccount.getId());
    }

    /**
     * Проверяет, достаточно ли средств на счете для выполнения перевода.
     *
     * @param actualBalance текущий баланс счета
     * @param amount        сумма перевода
     * @return true, если баланс достаточен, иначе false
     */
    private boolean hasSufficientBalance(BigDecimal actualBalance, BigDecimal amount) {
        if (actualBalance == null || amount == null) {
            throw new IllegalArgumentException("Balance and amount values cannot be null");
        }
        return actualBalance.compareTo(amount) >= 0;
    }
}
