package faang.school.accountservice.dto.tariff;

import lombok.Data;

import java.util.UUID;

@Data
public class TariffDto {
    private UUID id;
    private String type;
    private Double currentRate;
}
