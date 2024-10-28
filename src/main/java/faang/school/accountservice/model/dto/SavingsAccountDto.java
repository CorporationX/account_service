package faang.school.accountservice.model.dto;

import faang.school.accountservice.model.entity.TariffHistory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SavingsAccountDto {
    private Long id;

    @NotBlank @Positive
    private Long accountId;

    @NotBlank @Positive
    private Long tariffId;
    private List<TariffHistory> tariffHistory;
    private LocalDateTime lastDatePercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
