package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.model.enums.OperationType;
import faang.school.accountservice.model.event.RequestEvent;
import faang.school.accountservice.service.PaymentRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventListener extends AbstractEventListener<RequestEvent> implements MessageListener {

    private final PaymentRequestService paymentRequestService;

    public PaymentEventListener(ObjectMapper objectMapper,
                                PaymentRequestService paymentRequestService) {
        super(objectMapper);
        this.paymentRequestService = paymentRequestService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, RequestEvent.class, event -> {



            if (event.getOperationType() == OperationType.AUTHORIZATION) {
                paymentRequestService.authorize(event);
            }
        });
    }
}