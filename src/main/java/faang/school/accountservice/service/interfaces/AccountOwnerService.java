package faang.school.accountservice.service.interfaces;

import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.enums.OwnerType;

public interface AccountOwnerService {
    AccountOwner findOwner(Long ownerId, OwnerType ownerType);
}
