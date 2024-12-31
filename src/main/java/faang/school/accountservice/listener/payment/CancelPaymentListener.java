package faang.school.accountservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.request.CancelPaymentRequest;
import faang.school.accountservice.listener.abstracts.AbstractEventListener;
import faang.school.accountservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CancelPaymentListener extends AbstractEventListener<CancelPaymentRequest> {
    private final PaymentService paymentService;

    @Value("${spring.data.redis.channels.cancel-payment.request}")
    private String topicName;

    public CancelPaymentListener(ObjectMapper objectMapper,
                                 PaymentService paymentService) {
        super(objectMapper, CancelPaymentRequest.class);
        this.paymentService = paymentService;
    }

    @Override
    public void handleMessage(CancelPaymentRequest event) {
        paymentService.cancelPayment(event);
    }

    @Override
    public Topic getTopic() {
        return new ChannelTopic(topicName);
    }
}
