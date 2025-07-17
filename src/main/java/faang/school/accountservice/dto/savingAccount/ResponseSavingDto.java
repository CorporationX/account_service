package faang.school.accountservice.dto.savingAccount;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseSavingDto {

    private UUID id;
    private BigDecimal balance;
    private String currency;
    private String tariff;
    private long bet;  // тарифная ставка
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
