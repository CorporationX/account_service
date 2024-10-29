package faang.school.accountservice.config.redis.adapter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


@Configuration
public class DmsEventListenerAdapterParam extends AbstractEventListenerAdapter {
    public DmsEventListenerAdapterParam(
        @Value("${spring.data.redis.channels.dms-channel.name}") String topicName,
        @Qualifier("dmsEventListener") MessageListener listener
    ) {
        super(topicName, listener);
    }

    @Override
    @Bean("dmsEventTopic")
    public ChannelTopic getChannelTopic() {
        return this.channelTopic;
    }

    @Override
    @Bean("dmsEventListenerAdapter")
    public MessageListenerAdapter getAdapter() {
        return this.adapter;
    }
}
