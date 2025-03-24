package faang.school.accountservice.service;

import faang.school.accountservice.entity.Merchant;
import faang.school.accountservice.exception.EntityNotFoundException;
import faang.school.accountservice.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MerchantService {
    private final MerchantRepository merchantRepository;

    public Merchant findMerchantById(long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Merchant with id " + id + " not found")
                );
    }
}
