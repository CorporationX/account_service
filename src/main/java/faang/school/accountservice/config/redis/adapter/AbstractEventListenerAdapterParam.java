package faang.school.accountservice.config.redis.adapter;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;


public abstract class AbstractEventListenerAdapterParam implements EventListenerAdapterParam {
    protected final ChannelTopic channelTopic;
    protected final MessageListenerAdapter adapter;

    public AbstractEventListenerAdapterParam(String topicName, MessageListener listener) {
        this.channelTopic = new ChannelTopic(topicName);
        this.adapter = new MessageListenerAdapter(listener);
    }

    @Override
    public ChannelTopic getChannelTopic() {
        return channelTopic;
    };

    @Override
    public MessageListenerAdapter getAdapter() {
        return adapter;
    };

}
