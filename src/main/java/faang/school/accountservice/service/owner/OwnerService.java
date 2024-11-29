package faang.school.accountservice.service.owner;

import faang.school.accountservice.mapper.owner.OwnerMapper;
import faang.school.accountservice.repository.owner.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final OwnerMapper ownerMapper;

    public long getOwnerIdByName(String ownerName) {
        return ownerRepository.findIdByName(ownerName);
    }

    public long getCountOwnerAccounts(long ownerId) {
        return ownerRepository.countAccountsById(ownerId);
    }
}
