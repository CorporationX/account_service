package faang.school.accountservice.dto.account;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAccountDto {
    private UUID id;
    private String tariffHistory;
    private LocalDateTime lastInterestCalculationDate;
    private long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @NotNull(message = "Savings account accountId can't be null!")
    private UUID accountId;
}
