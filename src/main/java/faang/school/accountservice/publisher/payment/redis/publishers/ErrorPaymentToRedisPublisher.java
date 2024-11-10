package faang.school.accountservice.publisher.payment.redis.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.dto.payment.response.ErrorPaymentResponse;
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
public class ErrorPaymentToRedisPublisher extends AbstractToRedisPaymentPublisher<ErrorPaymentResponse> {
    @Value("${spring.data.redis.channel.error-payment.response}")
    private String topicName;

    public ErrorPaymentToRedisPublisher(RedisTemplate<String, Object> redisTemplate,
                                        ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, ErrorPaymentResponse.class);
    }

    @Override
    public void makeResponse(Object... args) {
        AuthPayment authPayment = (AuthPayment) args[0];
        setResponse(new ErrorPaymentResponse(authPayment.getId(), SUCCESS));
    }

    @Override
    public void makeErrorResponse(Object... args) {
        ErrorPaymentRequest errorPaymentRequest = (ErrorPaymentRequest) args[0];
        setResponse(new ErrorPaymentResponse(errorPaymentRequest.getOperationId(), FAILED));
    }
}
