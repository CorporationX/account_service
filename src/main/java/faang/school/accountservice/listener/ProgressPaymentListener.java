package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.dto.PendingDto;
import faang.school.accountservice.service.BalanceService;
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
public class ProgressPaymentListener implements MessageListener  {

    private final ObjectMapper objectMapper;
    private final BalanceService balanceService;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            List<PendingDto> pendingDto = objectMapper.readValue(message.getBody(), new TypeReference<>() {});
            balanceService.submitPayment(pendingDto);
        } catch (IOException exception) {
            log.error(exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
