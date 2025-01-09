package faang.school.accountservice.dto.cashback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashbackTariffDto {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}