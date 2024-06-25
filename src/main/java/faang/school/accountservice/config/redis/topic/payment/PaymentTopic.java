package faang.school.accountservice.config.redis.topic.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class PaymentTopic {

    @Value("${spring.data.redis.channel.payment.request}")
    private String paymentRequestChannelName;
    @Bean
    public ChannelTopic paymentRequestTopic() {
        return new ChannelTopic(paymentRequestChannelName);
    }
}
