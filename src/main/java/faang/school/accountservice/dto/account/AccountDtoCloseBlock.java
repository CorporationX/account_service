package faang.school.accountservice.dto.account;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDtoCloseBlock {
    @Positive(message = "Account Id must be greater than 0")
    private Long id;

    @Pattern(regexp = "^[0-9]{12,20}$", message = "Account number must be between 12 and 20 digits and contain only numbers.")
    private String accountNumber;
}