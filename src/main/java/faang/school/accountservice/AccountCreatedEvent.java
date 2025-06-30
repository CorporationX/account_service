package faang.school.accountservice;

import faang.school.accountservice.model.OwnerType;

public record AccountCreatedEvent(
        Long ownerId,
        OwnerType ownerType
) {}
