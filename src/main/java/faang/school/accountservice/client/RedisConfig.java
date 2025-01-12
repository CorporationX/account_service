package faang.school.accountservice.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.accountservice.listener.AuthorizationMessageEventListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;
    @Value("${spring.data.redis.channels.payment}")
    private String paymentChannel;

    @Value("${spring.data.redis.channels.payment-result}")
    private String paymentResultChannel;

    @Bean
    public JedisConnectionFactory connectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(configuration);
    }
    @Bean
    public RedisTemplate<String, Object> redisTemplate(ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Object.class));
        return template;
    }

    @Bean
    public ChannelTopic paymentChannelTopic(){
        return new ChannelTopic(paymentChannel);
    }

    @Bean
    public ChannelTopic paymentResultChannelTopic(){
        return new ChannelTopic(paymentResultChannel);
    }



    @Bean
    public RedisMessageListenerContainer container (AuthorizationMessageEventListener authorizationListener){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.addMessageListener(authorizationListener,paymentChannelTopic());
        return container;
    }
}
