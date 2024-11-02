package faang.school.accountservice.config.redis.topic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

@Configuration
public class RedisTopicFactory {
    @Bean
    public Topic pendingOperationTopic(@Value("${spring.data.redis.channel.pending_operation}") String name) {
        return new ChannelTopic(name);
    }

    @Bean
    public Topic checkingBalanceTopic(@Value("${spring.data.redis.channel.checking_balance}") String name) {
        return new ChannelTopic(name);
    }
}
