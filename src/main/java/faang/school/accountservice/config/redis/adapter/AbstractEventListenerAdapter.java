package faang.school.accountservice.config.redis.adapter;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

public abstract class AbstractEventListenerAdapter implements EventListenerAdapter {
    protected final ChannelTopic channelTopic;
    protected final MessageListenerAdapter adapter;

    public AbstractEventListenerAdapter(String topicName, MessageListener listener) {
        this.channelTopic = new ChannelTopic(topicName);
        this.adapter = new MessageListenerAdapter(listener);
    }

    @Override
    public abstract ChannelTopic getChannelTopic();

    @Override
    public abstract MessageListenerAdapter getAdapter();

}
