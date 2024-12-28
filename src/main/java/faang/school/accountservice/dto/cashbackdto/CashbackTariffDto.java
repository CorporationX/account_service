package faang.school.accountservice.dto.cashbackdto;

import faang.school.accountservice.model.CashbackMerchantMapping;
import faang.school.accountservice.model.CashbackOperationMapping;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CashbackTariffDto(
        Long id,
        LocalDateTime createdAt,
        List<CashbackOperationMapping> operationMappings,
        List<CashbackMerchantMapping> merchantMappings
) {
}