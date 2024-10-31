package faang.school.accountservice.dto.account;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SavingsAccountCreatedDto {

    @NotNull
    private Long accountId;

    @NotNull
    private String tariffType;
}
