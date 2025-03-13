package faang.school.accountservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TariffDto {
    private Long id;
    private String name;
    private List<Double> rateHistory;
}