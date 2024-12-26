package faang.school.accountservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransactionRequestDto(

        @Size(min = 12, max = 20, message = "Account number must be between 12 and 20 characters")
        String accountNumber,

        @DecimalMin(value = "0.01", message = "Amount should not be less than 0.01")
        @Digits(integer = 18, fraction = 2, message = "Amount should have 2 decimal places")
        BigDecimal amount
) {
}
