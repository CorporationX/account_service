package faang.school.accountservice.service.implementations;

import faang.school.accountservice.entity.AccountOwner;
import faang.school.accountservice.enums.OwnerType;
import faang.school.accountservice.repository.AccountOwnerRepository;
import faang.school.accountservice.service.interfaces.AccountOwnerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountOwnerServiceImpl implements AccountOwnerService {
    private final AccountOwnerRepository accountOwnerRepository;

    @Override
    public AccountOwner findOwner(Long ownerId, OwnerType ownerType) {
        return accountOwnerRepository.findByOwnerIdAndOwnerType(ownerId, ownerType)
                .orElseThrow(() -> new EntityNotFoundException("Owner with id: " + ownerId + " not found"));
    }
}
