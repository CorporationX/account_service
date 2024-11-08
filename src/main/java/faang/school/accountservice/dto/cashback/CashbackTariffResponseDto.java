package faang.school.accountservice.dto.cashback;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class CashbackTariffResponseDto {
    private UUID id;
    private String name;
    private List<CashbackOperationTypeDto> cashbackOperationTypes;
    private List<CashbackMerchantDto> cashbackMerchants;
    private LocalDateTime createdAt;
}
