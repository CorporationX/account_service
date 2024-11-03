package faang.school.accountservice.model.dto;

import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsAccountDto {
    @Null(message = "id must be null, when you create SavingsAccount", groups = {Create.class})
    private Long id;

    @Positive(message = "accountId must be positive, when you create SavingsAccount", groups = {Create.class})
    private Long accountId;

    @Positive(message = "tariff id must be positive, when you create SavingsAccount", groups = {Create.class})
    private Long tariffId;
    private Double rate;
    private LocalDateTime lastDatePercent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public interface Create {

    }
}

