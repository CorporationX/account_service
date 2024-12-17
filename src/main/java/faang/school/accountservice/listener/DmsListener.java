package faang.school.accountservice.listener;

import faang.school.accountservice.dto.PaymentOperationDto;
import faang.school.accountservice.service.DmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DmsListener implements MessageListener {
    private final DmsService dmsService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        Jackson2JsonRedisSerializer<PaymentOperationDto> serializer =
                new Jackson2JsonRedisSerializer<>(PaymentOperationDto.class);

        PaymentOperationDto paymentOperationDto = serializer.deserialize(message.getBody());

        if (paymentOperationDto != null) {
            log.info("Получили сообщение: {}", paymentOperationDto);
            Long id = paymentOperationDto.getId();
            dmsService.sendTest(id);
        }
    }
}