package faang.school.accountservice.listener.abstracts;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.Topic;

public interface EventMessageListener extends MessageListener {
    Topic getTopic();
}
