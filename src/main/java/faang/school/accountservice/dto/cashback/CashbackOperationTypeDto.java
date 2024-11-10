package faang.school.accountservice.dto.cashback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashbackOperationTypeDto {
    private UUID id;
    private UUID cashbackTariffId;
    private String operationType;
    private BigDecimal cashbackPercentage;
    private LocalDateTime createdAt;
}