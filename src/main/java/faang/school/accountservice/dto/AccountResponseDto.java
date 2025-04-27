package faang.school.accountservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.accountservice.enums.AccountStatus;
import faang.school.accountservice.enums.AccountType;
import faang.school.accountservice.enums.Currency;
import faang.school.accountservice.enums.OwnerType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountResponseDto(Long id,
                                 String accountNumber,
                                 Long ownerId,
                                 OwnerType ownerType,
                                 AccountType accountType,
                                 Currency currency,
                                 AccountStatus accountStatus,
                                 LocalDateTime createdAt,
                                 LocalDateTime updatedAt,
                                 LocalDateTime closedAt) {
}
