package faang.school.accountservice.dto.account_owner;

import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;

public record AccountOwnerDto(
        @NotNull Long ownerId,
        @NotNull OwnerType ownerType
) {}
