package faang.school.accountservice.dto.cashback;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CashbackTariffShortResponseDto {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
}
