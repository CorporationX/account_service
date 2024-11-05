package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.exception.DataValidationException;
import faang.school.accountservice.model.enums.OperationType;
import faang.school.accountservice.model.event.PaymentEvent;
import faang.school.accountservice.service.PaymentRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventListener extends AbstractEventListener<PaymentEvent> implements MessageListener {

    private final PaymentRequestService paymentRequestService;

    public PaymentEventListener(ObjectMapper objectMapper,
                                PaymentRequestService paymentRequestService) {
        super(objectMapper);
        this.paymentRequestService = paymentRequestService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, PaymentEvent.class, event -> {
            switch (event.getOperationType()) {
                case AUTHORIZATION -> paymentRequestService.authorize(event);
                case CANCEL -> paymentRequestService.cancel(event);
                case CLEARING -> paymentRequestService.clearing(event);
                default -> throw new DataValidationException(String.format("Unknown operation type: %s", event.getOperationType()));
            }
        });
    }
}