package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountDto {
    @NotNull
    private Long id;

    @NotNull
    private Long accountId;

    @NotNull
    private int version;

    private LocalDateTime lastInterestCalculationDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
