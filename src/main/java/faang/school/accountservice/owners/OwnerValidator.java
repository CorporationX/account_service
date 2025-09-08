package faang.school.accountservice.owners;

import faang.school.accountservice.enums.OwnerType;

public interface OwnerValidator {
    OwnerType getOwnerType();

    boolean checkOwnerId(Long id);
}
