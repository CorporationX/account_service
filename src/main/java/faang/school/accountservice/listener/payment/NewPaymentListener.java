package faang.school.accountservice.listener.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.event.NewPaymentEvent;
import faang.school.accountservice.exception.ListenerException;
import faang.school.accountservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewPaymentListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public NewPaymentListener(PaymentService paymentService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.data.channel.payment.new.name}", groupId = "${spring.data.kafka.group-id}")
    public void listen(String event) {
        if (event == null || event.trim().isEmpty()) {
            log.error("Received empty or null event");
            return;
        }

        try {
            NewPaymentEvent newPaymentEvent = objectMapper.readValue(event, NewPaymentEvent.class);
            log.info("Received new PaymentEvent {}", event);
            paymentService.authorizePayment(newPaymentEvent.getUserId(), newPaymentEvent.getPaymentId());
        } catch (JsonProcessingException e) {
            log.error("Error processing event JSON: {}", event, e);
            throw new SerializationException(e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while processing event: {}", event, e);
            throw new ListenerException(e.getMessage());
        }
    }
}