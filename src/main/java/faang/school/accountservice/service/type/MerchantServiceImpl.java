package faang.school.accountservice.service.type;

import faang.school.accountservice.dto.MappingTypeDto;
import faang.school.accountservice.entity.type.Merchant;
import faang.school.accountservice.mapper.MappingTypeMapper;
import faang.school.accountservice.repository.MerchantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MerchantServiceImpl implements MerchantService {
    private final MerchantRepository merchantRepository;
    private final MappingTypeMapper mappingTypeMapper;

    @Override
    public MappingTypeDto get(Long id) {
        return merchantRepository.findById(id)
                .map(mappingTypeMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found with id: " + id));
    }

    @Override
    public MappingTypeDto create(MappingTypeDto mappingTypeDto) {
        Merchant merchant = new Merchant();
        merchant.setName(mappingTypeDto.getName());
        merchant = merchantRepository.save(merchant);

        return mappingTypeMapper.toDto(merchant);
    }
    @Override
    public MappingTypeDto update(MappingTypeDto mappingTypeDto) {
        Merchant merchant = merchantRepository.findById(mappingTypeDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found with id: " + mappingTypeDto.getId()));

            merchant.setName(mappingTypeDto.getName());
            merchantRepository.save(merchant);

            return mappingTypeMapper.toDto(merchant);
    }
    @Override
    public void delete(Long id) {
        Merchant merchant = merchantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found with id: " + id));
        merchantRepository.delete(merchant);
    }
}
