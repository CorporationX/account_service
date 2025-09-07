package faang.school.accountservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PaymentFailedEvent {

    private UUID requestId;
    private PaymentMessageDto message;
    private String reason;
}