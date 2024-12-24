package faang.school.accountservice.message;

import faang.school.accountservice.dto.AuthorizationEvent;
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
        // todo: В AuthorizationEvent мы передаем только айди получателя, но по сути нам так же нужен отправитель
        //  потому что по айди отправителя мы сможем проверять, достаточно ли у него средств. Поэтому сейчас тут лажа
        //  написана. Надо потом будет добавить senderId и по нему доставать этот аккаунт и делать проверки
        Account account = accountRepository.findById(event.getRecipientId()).orElseThrow(
                () -> new AccountNotFoundException("Account not found")
        );

        if (calculateBalanceCapacity(account.getBalance().getActualBalance(), event.getAmount())){
            // todo: решить какой объект отправим для отмены
            kafkaTemplate.send("cancel-payment-topic", event);
            log.info("Payment cancelled because balance not enough");
            throw new BalanceLimitException("Balance is not enough");
        }
        log.info("Balance capacity is enough");

        balanceService.authorizePayment(account, event.getAmount());
        log.info("Balance was authorized for account {}", account.getId());

        // todo: решить какой объект отправим для успешной авторизации
        kafkaTemplate.send("successful-payment-auth-topic", event);
        log.info("Auth event was sent");
    }

    private boolean calculateBalanceCapacity(BigDecimal actualBalance, BigDecimal authBalance) {
        if (actualBalance == null || authBalance == null) {
            throw new IllegalArgumentException("Balance values cannot be null");
        }

        return authBalance.compareTo(actualBalance) >= 0;
    }
}
