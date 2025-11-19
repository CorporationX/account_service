package faang.school.accountservice.model;

import jakarta.validation.constraints.NotNull;

public record OwnerDto(
        Long id,
        @NotNull
        Long ownerId,
        @NotNull
        OwnerPerson ownerPerson

) {
}