package faang.school.accountservice.dto;

import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {

    private Long id;

    @Size(min = 12, message = "Account number must be more or equals to 12 numbers")
    @Size(max = 20, message = "Account number must be less or equals to 20 numbers")
    @Pattern(regexp = "\\d+", message = "Account number must contains only numbers")
    private String number;

    @NotNull(message = "Owner Id must be set and can't be 0")
    @Min(value = 1, message = "Owner Id must be set and can't be 0")
    private Long ownerId;

    private AccountType type;
    private Currency currency;
    private AccountStatus status;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    private Integer version;
}
