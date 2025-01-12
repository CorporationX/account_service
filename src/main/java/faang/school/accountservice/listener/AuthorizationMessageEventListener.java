package faang.school.accountservice.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.event.AuthorizationMessageEvent;
import faang.school.accountservice.service.balance.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthorizationMessageEventListener implements MessageListener {
    private final ObjectMapper objectMapper;

    private final BalanceService balanceService;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        AuthorizationMessageEvent authorization = readMessage(message);

        balanceService.reserveMoneyOnAuthorisationBalance(
                authorization);
    }

    public AuthorizationMessageEvent readMessage(Message message) {
        String stringMessage = new String(message.getBody());

        try {
            return objectMapper.readValue(stringMessage, AuthorizationMessageEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
