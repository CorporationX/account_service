package faang.school.accountservice.publis.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.PaymentDto;
import faang.school.accountservice.publis.listener.payment.handlers.PaymentStatusHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final List<PaymentStatusHandler> handlers;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        log.info("Get message: {}", messageBody);
        try {
            PaymentDto paymentDto = objectMapper.readValue(messageBody, PaymentDto.class);

            handlers.stream()
                    .filter(handler -> Objects.equals(handler.requiredStatus(), paymentDto.getStatus()))
                    .forEach(handler -> handler.handlePayment(paymentDto));
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            throw new IllegalArgumentException(exception);
        }
    }
}
