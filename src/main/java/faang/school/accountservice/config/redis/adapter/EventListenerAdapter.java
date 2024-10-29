package faang.school.accountservice.config.redis.adapter;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

public interface EventListenerAdapter {

    ChannelTopic getChannelTopic();

    MessageListenerAdapter getAdapter();
}
