package faang.school.accountservice.config.redis.adapter;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

public interface EventListenerAdapterConfig {

    ChannelTopic getChannelTopic();

    MessageListenerAdapter getAdapter();
}
