package faang.school.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDto {
    private UUID id;
    private String accountNumber;
    @Positive(message = "Owner ID must be a positive number!")
    private long ownerId;
    @NotBlank(message = "Owner type must be provided!")
    private String ownerType;
    @NotBlank(message = "Account type must be provided!")
    private String accountType;
    @NotBlank(message = "Currency must be provided!")
    private String currency;
    @NotBlank(message = "Status must be provided!")
    private String status;
    @PositiveOrZero(message = "Balance must be a non-negative number!")
    private BigDecimal balance;
    @PositiveOrZero(message = "Transaction limit must be a non-negative number!")
    private BigDecimal transactionLimit;
    private long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

}
