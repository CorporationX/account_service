package faang.school.accountservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.ErrorPaymentRequest;
import faang.school.accountservice.listener.abstracts.AbstractEventListener;
import faang.school.accountservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ErrorPaymentListener extends AbstractEventListener<ErrorPaymentRequest> {
    private final PaymentService paymentService;

    @Value("${spring.data.redis.channels.error-payment.request}")
    String topicName;

    public ErrorPaymentListener(ObjectMapper objectMapper,
                                PaymentService paymentService) {
        super(objectMapper, ErrorPaymentRequest.class);
        this.paymentService = paymentService;
    }

    @Override
    public void handleMessage(ErrorPaymentRequest event) {
        paymentService.errorPayment(event);
    }

    @Override
    public Topic getTopic() {
        return new ChannelTopic(topicName);
    }
}
