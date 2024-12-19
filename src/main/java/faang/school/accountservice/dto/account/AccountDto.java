package faang.school.accountservice.dto.account;

import faang.school.accountservice.entity.account.enums.AccountStatus;
import faang.school.accountservice.entity.account.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private String paymentNumber;

    @NotNull
    private AccountType type;
    @NotNull
    private Currency currency;
    @NotNull
    private AccountStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

}
