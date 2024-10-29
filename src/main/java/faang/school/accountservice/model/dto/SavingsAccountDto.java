package faang.school.accountservice.model.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SavingsAccountDto {
    private Long id;

    @Positive
    private Long accountId;

    @Positive
    private Long tariffId;
    private LocalDateTime lastDatePercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
