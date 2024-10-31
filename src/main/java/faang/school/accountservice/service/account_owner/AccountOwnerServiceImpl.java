package faang.school.accountservice.service.account_owner;

import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.repository.AccountOwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountOwnerServiceImpl implements AccountOwnerService {
    private final AccountOwnerRepository accountOwnerRepository;

    @Override
    public AccountOwner getAccountOwnerById(Long accountOwnerId) {
        return accountOwnerRepository.findById(accountOwnerId)
                .orElseThrow(() -> new EntityNotFoundException("Account owner with id %d not found".formatted(accountOwnerId)));
    }

    @Override
    public AccountOwner getOrCreateAccountOwner(Long ownerId, OwnerType ownerType) {
        AccountOwner accountOwner = accountOwnerRepository.findByOwnerIdAndOwnerType(ownerId, ownerType);
        if (accountOwner != null) {
            return accountOwner;
        }
        AccountOwner newAccountOwner = AccountOwner.builder()
                .ownerId(ownerId)
                .ownerType(ownerType)
                .build();
        return accountOwnerRepository.save(newAccountOwner);
    }
}
