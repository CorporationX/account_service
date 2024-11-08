package faang.school.accountservice.service.account_owner;

import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.model.AccountOwner;

public interface AccountOwnerService {

    AccountOwner getAccountOwnerById(Long accountOwnerId);

    AccountOwner getOrCreateAccountOwner(Long ownerId, OwnerType ownerType);
}
