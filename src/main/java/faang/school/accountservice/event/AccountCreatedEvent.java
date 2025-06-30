package faang.school.accountservice.event;

import faang.school.accountservice.model.OwnerType;

public record AccountCreatedEvent(
        Long ownerId,
        OwnerType ownerType
) {}
