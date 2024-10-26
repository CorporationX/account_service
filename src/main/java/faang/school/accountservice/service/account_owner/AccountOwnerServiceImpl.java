package faang.school.accountservice.service.account_owner;

import faang.school.accountservice.model.AccountOwner;
import faang.school.accountservice.repository.AccountOwnerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountOwnerServiceImpl implements AccountOwnerService {
    private final AccountOwnerRepository accountOwnerRepository;

    @Override
    public AccountOwner getAccountOwnerById(Long accountOwnerId) {
        return accountOwnerRepository.findById(accountOwnerId)
                .orElseThrow(() -> new EntityNotFoundException("Account owner with id %d not found".formatted(accountOwnerId)));
    }
}
