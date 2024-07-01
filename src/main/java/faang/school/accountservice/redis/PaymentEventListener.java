package faang.school.accountservice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.PaymentEventDto;
import faang.school.accountservice.service.account.payment.PaymentOperationHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final List<PaymentOperationHandler> handlers;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            PaymentEventDto paymentEvent = objectMapper.readValue(message.getBody(), PaymentEventDto.class);

            handlers.stream()
                    .filter(handler -> handler.getRequiredOperationType().name().equals(paymentEvent.getType()))
                    .forEach(handler -> handler.handlePaymentOperation(paymentEvent));
        } catch (IOException e) {
            log.error("Received message decoding failed: {1}", e);
        }
    }
}
