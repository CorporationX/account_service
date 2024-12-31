package faang.school.accountservice.publisher.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.dto.payment.response.CancelPaymentResponse;
import faang.school.accountservice.model.payment.Payment;
import faang.school.accountservice.publisher.payment.abstracts.AbstractPaymentPublisher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import static faang.school.accountservice.model.payment.PaymentResponseStatus.FAILED;
import static faang.school.accountservice.model.payment.PaymentResponseStatus.SUCCESS;

@Slf4j
@Getter
@Component
public class CancelPaymentPublisher extends AbstractPaymentPublisher<CancelPaymentResponse> {

    @Value("${spring.data.redis.channels.cancel-payment.response}")
    private String topicName;

    public CancelPaymentPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper, CancelPaymentResponse.class);
    }

    @Override
    public void makeResponse(Object... args) {
        Payment authPayment = (Payment) args[0];
        setResponse(new CancelPaymentResponse(authPayment.getId(), SUCCESS));
    }

    @Override
    public void makeErrorResponse(Object... args) {
        CancelPaymentRequest cancelPaymentRequest = (CancelPaymentRequest) args[0];
        setResponse(new CancelPaymentResponse(cancelPaymentRequest.getOperationId(), FAILED));
    }
}
