package faang.school.accountservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.event.ClearPaymentEvent;
import faang.school.accountservice.service.payment.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ClearPaymentListener extends AbstractPaymentListener<ClearPaymentEvent> {

    private final PaymentService paymentService;

    public ClearPaymentListener(PaymentService paymentService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.paymentService = paymentService;
    }

    @Override
    protected Class<ClearPaymentEvent> getEventClass() {
        return ClearPaymentEvent.class;
    }

    @Override
    protected void processEvent(ClearPaymentEvent event) {
        paymentService.clearPayment(event.getPaymentId());
    }

    @KafkaListener(topics = "${spring.data.channel.payment.clear.name}", groupId = "${spring.data.kafka.group-id}")
    public void listenEvent(String event) {
        listen(event);
    }
}