package faang.school.accountservice.config.redis;

import faang.school.accountservice.dto.PaymentOperationDto;
import lombok.Data;

@Data
public class RedisMessage {
    private String correlationId;
    private String type;
    private PaymentOperationDto payload;
    private String error;
}