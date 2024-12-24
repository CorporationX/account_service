package faang.school.accountservice.dto.cashbackdto;

import faang.school.accountservice.model.CashbackMerchantMapping;
import faang.school.accountservice.model.CashbackOperationMapping;

import java.time.LocalDateTime;
import java.util.List;

public record CashbackTariffDto(
        Long id,
        LocalDateTime createdAt,
        List<CashbackOperationMapping> operationMappings,
        List<CashbackMerchantMapping> merchantMappings
) {
}