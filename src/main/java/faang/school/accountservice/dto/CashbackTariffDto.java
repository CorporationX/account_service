package faang.school.accountservice.dto;

import jakarta.validation.constraints.Null;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CashbackTariffDto {

    @Null
    private Long id;

    @Null
    private Set<Long> accounts;

    @Null
    private Set<Long> operationMappings;

    @Null
    private Set<Long> merchantMappings;

    private LocalDateTime createdAt;
}
