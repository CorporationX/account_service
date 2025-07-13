package faang.school.accountservice.config.kafka;

import faang.school.accountservice.exception.operation.payment.AccountCurrencyMismatchException;
import faang.school.accountservice.exception.operation.payment.InvalidAccountOwnerException;
import faang.school.accountservice.exception.operation.payment.UnauthorizedAccountAccessException;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
// TODO: всё исправить
public class KafkaErrorHandlingConfig {

    // KafkaTemplate нужен, если хотим публиковать в DLT
    @Bean
    public DefaultErrorHandler defaultErrorHandler(KafkaTemplate<String, Object> template) {

        // куда отправлять безнадёжные сообщения
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(template,
                        (rec, ex) -> new TopicPartition("payment-authorization-dlt",
                                rec.partition()));

        // 3 повтора с паузой 1 с
        FixedBackOff backOff = new FixedBackOff(1000L, 3);

        DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);

        // какие исключения **не** пробовать повторять
        handler.addNotRetryableExceptions(
                AccountCurrencyMismatchException.class,
                InvalidAccountOwnerException.class,
                UnauthorizedAccountAccessException.class);

        return handler;
    }
}
