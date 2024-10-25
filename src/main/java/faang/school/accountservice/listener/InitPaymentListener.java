package faang.school.accountservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.service.BalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class InitPaymentListener implements MessageListener {

    private final ObjectMapper objectMapper;
    private final BalanceService balanceService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            PendingDto pendingDto = objectMapper.readValue(message.getBody(), PendingDto.class);
            balanceService.paymentAuthorization(pendingDto);
        } catch (IOException exception) {
            log.error(exception.getMessage(), exception);
            throw new RuntimeException(exception);
        }
    }
}
