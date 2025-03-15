package faang.school.accountservice.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record RateChangeRequest(
        @NotNull Long tariffId,
        @NotNull BigDecimal oldRate,
        @NotNull BigDecimal newRate,
        @NotNull @FutureOrPresent LocalDate scheduledDate
) {}