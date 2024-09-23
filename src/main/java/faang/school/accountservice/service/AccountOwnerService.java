package faang.school.accountservice.service;

import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountOwnerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class AccountOwnerService {

    private final AccountOwnerRepository accountOwnerRepository;

    @Transactional
    public AccountOwner getAndCreateIfNecessary(Long ownerId, OwnerType type) {
        return accountOwnerRepository.getAndCreateIfNecessary(
                ownerId,
                type.toString());
    }
}
