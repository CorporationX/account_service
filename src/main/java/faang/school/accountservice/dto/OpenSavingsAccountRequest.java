package faang.school.accountservice.dto;

import lombok.Data;

@Data
public class OpenSavingsAccountRequest {
    private Long accountId;
    private Long initialTariffId;
}