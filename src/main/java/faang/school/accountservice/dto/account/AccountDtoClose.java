package faang.school.accountservice.dto.account;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoClose {
    @Positive(message = "Account Id must be greater than 0")
    private Long id;

    @Pattern(regexp = "^[0-9]{12,20}$", message = "Account number must be between 12 and 20 digits and contain only numbers.")
    private String accountNumber;

    @NotNull
    private LocalDateTime closedAt;
}
