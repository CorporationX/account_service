package faang.school.accountservice.dto;

import faang.school.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenSavingsAccountRequest {
    private Long ownerId;

    private Currency currency;

    private Long initialTariffId;
}
