package faang.school.accountservice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.payment.PaymentDto;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final BalanceService balanceService;
    private final ObjectMapper objectMapper;

    public void handlePaymentListener(@Payload Map<String, Object> message, Acknowledgment ack) {

        PaymentDto paymentDto = objectMapper.convertValue(message, PaymentDto.class);

        paymentDto.typeOperation().process(balanceService, paymentDto);

        ack.acknowledge();

        log.info("The message has been received from Kafka and processed. accountId - {}, amount - {}, type - {}",
                paymentDto.accountId(), paymentDto.amount(), paymentDto.typeOperation());
    }


}
