package faang.school.accountservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Событие, которое публикуется при неудачной обработке платежа (FAILED).
 * <p>
 * Содержит информацию о заявке, DTO платежа и причине ошибки.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PaymentFailedEvent {

    private UUID requestId;
    private PaymentMessageDto message;
    private String reason;
}