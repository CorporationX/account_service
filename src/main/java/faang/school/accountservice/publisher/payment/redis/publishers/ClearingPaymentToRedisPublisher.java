package faang.school.accountservice.publisher.payment.redis.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.ClearingPaymentRequest;
import faang.school.accountservice.dto.payment.response.ClearingPaymentResponse;
import faang.school.accountservice.entity.auth.payment.AuthPayment;
import faang.school.accountservice.publisher.payment.redis.AbstractToRedisPaymentPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static faang.school.accountservice.enums.payment.PaymentStatus.FAILED;
import static faang.school.accountservice.enums.payment.PaymentStatus.SUCCESS;

@Slf4j
@Getter
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
@Component
public class ClearingPaymentToRedisPublisher extends AbstractToRedisPaymentPublisher<ClearingPaymentResponse> {
    @Value("${spring.data.redis.channel.clearing-payment.response}")
    private String topicName;

    public ClearingPaymentToRedisPublisher(RedisTemplate<String, Object> redisTemplate,
                                           ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, ClearingPaymentResponse.class);
    }

    @Override
    public void makeResponse(Object... args) {
        AuthPayment authPayment = (AuthPayment) args[0];
        setResponse(new ClearingPaymentResponse(authPayment.getId(), SUCCESS));
    }

    @Override
    public void makeErrorResponse(Object... args) {
        ClearingPaymentRequest clearingPaymentRequest = (ClearingPaymentRequest) args[0];
        setResponse(new ClearingPaymentResponse(clearingPaymentRequest.getOperationId(), FAILED));
    }
}
