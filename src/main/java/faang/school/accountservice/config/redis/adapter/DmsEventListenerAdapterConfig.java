package faang.school.accountservice.config.redis.adapter;

import faang.school.accountservice.listener.DmsEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class DmsEventListenerAdapterConfig extends AbstractEventListenerAdapterConfig {
    public DmsEventListenerAdapterConfig(
        @Value("${spring.data.redis.channels.dms-channel.name}") String topicName,
        DmsEventListener listener
    ) {
        super(topicName, listener);
    }
}
