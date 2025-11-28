package faang.school.accountservice.dto.owner;

import faang.school.accountservice.enums.OwnerType;
import jakarta.validation.constraints.NotNull;

public record OwnerDto(
        Long id,
        @NotNull
        Long ownerId,
        @NotNull
        OwnerType ownerType

) {
}