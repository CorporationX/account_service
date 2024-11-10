package faang.school.accountservice.listener.redis.listeners.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.AuthPaymentRequest;
import faang.school.accountservice.exception.ApiException;
import faang.school.accountservice.listener.redis.abstracts.AbstractEventListener;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
@Component
public class AuthPaymentRedisListener extends AbstractEventListener<AuthPaymentRequest> {
    private final BalanceService balanceService;

    @Value("${spring.data.redis.channel.auth-payment.request}")
    private String topicName;

    public AuthPaymentRedisListener(ObjectMapper objectMapper,
                                    BalanceService balanceService) {
        super(objectMapper, AuthPaymentRequest.class);
        this.balanceService = balanceService;
    }

    @Override
    public void saveEvent(AuthPaymentRequest event) {
        balanceService.authorizePayment(event);
    }

    @Override
    public Topic getTopic() {
        return new ChannelTopic(topicName);
    }

    @Override
    public void handleException(Exception exception) {
        log.error("Unexpected error, listen topic: {}", topicName, exception);
        throw new ApiException(exception.getMessage(), INTERNAL_SERVER_ERROR);
    }
}
