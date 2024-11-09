package faang.school.accountservice.config.redis.adapter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Component
public class DmsEventListenerAdapterParam extends AbstractEventListenerAdapterParam {
    public DmsEventListenerAdapterParam(
        @Value("${spring.data.redis.channels.dms-channel.name}") String topicName,
        @Qualifier("dmsEventListener") MessageListener listener
    ) {
        super(topicName, listener);
    }
}
