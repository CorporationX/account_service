package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private Long id;

    @Size(min = 12, max = 20, message = "An account must have a number with size between 12 and 20 symbols.")
    private String number;

    private Long ownerUserId;

    private Long ownerProjectId;

    @NotNull(message = "An account must have a type.")
    private AccountType type;

    @NotNull(message = "An account must have a currency.")
    private Currency currency;

    @NotNull(message = "An account must have a status.")
    private AccountStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime closedAt;
}
