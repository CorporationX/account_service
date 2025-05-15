package faang.school.accountservice.dto.savingsaccount;

import lombok.Data;

@Data
public class OpenSavingsAccountDto {
    private String accountId;
    private Long initialTariffId;
}
