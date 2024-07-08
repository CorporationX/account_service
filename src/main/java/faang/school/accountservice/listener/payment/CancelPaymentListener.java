package faang.school.accountservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.event.CancelPaymentEvent;
import faang.school.accountservice.service.payment.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CancelPaymentListener extends AbstractPaymentListener<CancelPaymentEvent> {

    private final PaymentService paymentService;

    public CancelPaymentListener(PaymentService paymentService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.paymentService = paymentService;
    }

    @Override
    protected Class<CancelPaymentEvent> getEventClass() {
        return CancelPaymentEvent.class;
    }

    @Override
    protected void processEvent(CancelPaymentEvent event) {
        paymentService.cancelPayment(event.getUserId(), event.getPaymentId());
    }

    @KafkaListener(topics = "${spring.data.channel.payment.cancel.name}", groupId = "${spring.data.kafka.group-id}")
    public void listenEvent(String event) {
        listen(event);
    }
}