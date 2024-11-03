package faang.school.accountservice.integration.config.listener;

import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Getter
@Component
@Profile("testNoLiquibase")
public class RedisCheckingBalanceListener implements MessageListener {
    private String receivedMessage;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        receivedMessage = message.toString();
    }
}
