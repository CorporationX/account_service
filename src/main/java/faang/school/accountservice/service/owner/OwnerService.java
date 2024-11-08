package faang.school.accountservice.service.owner;

import faang.school.accountservice.entity.owner.Owner;
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

    public Owner getOwnerByName(String ownerName) {
        return ownerRepository.findByName(ownerName)
                .orElseThrow(() -> new EntityNotFoundException("Owner with name " + ownerName + " not found"));
    }

    public int getCountOwnerAccounts() {
        return ownerRepository.countAccounts();
    }
}
