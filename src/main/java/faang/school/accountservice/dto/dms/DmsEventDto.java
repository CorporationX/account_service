package faang.school.accountservice.dto.dms;

import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.DmsTypeOperation;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DmsEventDto{
    Long requestId;
    Long senderId;
    Long receiverId;
    BigDecimal amount;
    Currency currency;
    DmsTypeOperation typeOperation;
    LocalDateTime clearScheduledAt;
}
