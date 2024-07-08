package faang.school.accountservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.event.NewPaymentEvent;
import faang.school.accountservice.service.payment.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NewPaymentListener extends AbstractPaymentListener<NewPaymentEvent> {

    private final PaymentService paymentService;

    public NewPaymentListener(PaymentService paymentService, ObjectMapper objectMapper) {
        super(objectMapper);
        this.paymentService = paymentService;
    }

    @Override
    protected Class<NewPaymentEvent> getEventClass() {
        return NewPaymentEvent.class;
    }

    @Override
    protected void processEvent(NewPaymentEvent event) {
        paymentService.authorizePayment(event.getUserId(), event.getPaymentId());
    }

    @KafkaListener(topics = "${spring.data.channel.payment.new.name}", groupId = "${spring.data.kafka.group-id}")
    public void listenEvent(String event) {
        listen(event);
    }
}