package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;

    @NotNull(message = "Owner ID must not be null")
    private long ownerId;

    @NotNull(message = "Owner type must not be null")
    private OwnerType ownerType;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String accountNumber;

    @NotNull(message = "Type account must not be null")
    private AccountType type;

    @NotNull(message = "Type currency must not be null")
    private Currency currency;

    @NotNull(message = "Status must not be null")
    private AccountStatus status;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant createdAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant updatedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Instant closedAt;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private long version;

}