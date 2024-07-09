package faang.school.accountservice.publisher;

import faang.school.accountservice.dto.TariffRateChangeEvent;
import faang.school.accountservice.mapper.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class TariffRateChangePublisher extends AbstractPublisher<TariffRateChangeEvent>{
    public TariffRateChangePublisher(RedisTemplate<String, Object> redisTemplate,
                                     @Value("${spring.data.redis.channels.tariff_rate_change_channel.name}") String channelName,
                                     JsonMapper<TariffRateChangeEvent> jsonMapper) {
        super(redisTemplate, channelName, jsonMapper);
    }
}
