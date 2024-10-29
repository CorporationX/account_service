package faang.school.accountservice.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.model.operation.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEventDto {
    private UUID id;
    private String amount;
    private Currency currency;
    private UUID accountFromId;
    private UUID accountToId;
    private PaymentStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime clearScheduledAt;
}

