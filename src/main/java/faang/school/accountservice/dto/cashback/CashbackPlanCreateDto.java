package faang.school.accountservice.dto.cashback;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CashbackPlanCreateDto {
    @NotEmpty(message = "Правила начисления кэшбэка не должны быть пустыми")
    private List<@Positive @NotNull Long> rulesIds;
    private String description;
}
