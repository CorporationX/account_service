package faang.school.accountservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAccountDto {

    private List<BigDecimal> bettingHistory;
    private String number;

    @NotNull
    @Positive
    private Long accountId;

    @NotNull
    @Positive
    private Long tariffId;

    @NotNull
    @Positive
    private Long holderUserId;

    @NotNull
    @Positive
    private Long holderUserProjectId;
}
