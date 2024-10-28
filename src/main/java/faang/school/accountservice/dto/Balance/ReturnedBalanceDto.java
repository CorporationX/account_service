package faang.school.accountservice.dto.Balance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnedBalanceDto {

    @PositiveOrZero
    private long id;

    @PositiveOrZero
    private long authorizationBalance;

    @PositiveOrZero
    private long actualBalance;

    @NotNull
    @PastOrPresent
    private LocalDateTime createdAt;

    @NotNull
    @PastOrPresent
    private LocalDateTime updatedAt;

    @PositiveOrZero
    private int version;

    @PositiveOrZero
    private long accountId;
}
