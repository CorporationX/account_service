package faang.school.accountservice.model.cashback;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ReadCashbackTariffDto(
        long id,
        String name,
        List<OperationDto> operations,
        List<MerchantDto> merchants,

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime createdAt
) {
}
