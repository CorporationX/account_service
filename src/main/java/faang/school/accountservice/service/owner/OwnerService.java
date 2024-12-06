package faang.school.accountservice.service.owner;

import faang.school.accountservice.mapper.owner.OwnerMapper;
import faang.school.accountservice.repository.owner.OwnerRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public void getOwnerByName(String ownerName) {
         ownerRepository.findByName(ownerName).orElseThrow(
                ()-> new EntityNotFoundException("Owner not found: name - {} " + ownerName));
    }

}
