package faang.school.accountservice.publis.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.PaymentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener implements MessageListener {
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String messageBody = new String(message.getBody());
        log.info("Get message: {}", messageBody);
        try {
            PaymentEventDto paymentEventDto = objectMapper.readValue(messageBody, PaymentEventDto.class);
            System.out.println(paymentEventDto);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            throw new IllegalArgumentException(exception);
        }
    }
}
