package faang.school.accountservice.dto;

import faang.school.accountservice.enums.*;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Data
@Builder
public class AccountDto {
    private long id;
    private BigInteger number;
    @NonNull
    private OwnerType ownerType;
    private Long ownerProjectId;
    private Long ownerUserId;
    @NonNull
    private AccountType accountType;
    private TariffType tariffType;
    @NonNull
    private Currency currency;
    private AccountStatus accountStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;

}
