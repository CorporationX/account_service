package faang.school.accountservice.service;

import faang.school.accountservice.dto.MerchantMappingDto;
import faang.school.accountservice.dto.MerchantMappingUpdateDto;
import faang.school.accountservice.entity.MerchantMapping;
import faang.school.accountservice.mapper.MerchantMappingMapper;
import faang.school.accountservice.repository.MerchantMappingRepository;
import faang.school.accountservice.util.PercentageCalculator;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MerchantMappingServiceImpl implements MerchantMappingService {

    private final MerchantMappingRepository merchantMappingRepository;
    private final MerchantMappingMapper merchantMappingMapper;
    private final PercentageCalculator percentageCalculator;

    @Override
    public MerchantMappingDto createMerchantMapping(MerchantMappingDto dto) {
        MerchantMapping merchantMapping = merchantMappingMapper.toEntity(dto);
        merchantMapping = merchantMappingRepository.save(merchantMapping);
        return merchantMappingMapper.toDto(merchantMapping);
    }

    @Override
    public MerchantMappingDto getMerchantMappingById(long id) {
        return merchantMappingRepository.findById(id)
                .map(merchantMappingMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Merchant mapping with id %d not found".formatted(id)));
    }

    @Override
    @Transactional
    public MerchantMappingDto updateMerchantMapping(MerchantMappingUpdateDto merchantMappingUpdateDto) {
        long id = merchantMappingUpdateDto.getId();
        MerchantMapping merchantMapping = merchantMappingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Merchant mapping with id %d not found".formatted(id)));
        merchantMappingMapper.updateMerchantMapping(merchantMappingUpdateDto, merchantMapping);
        return merchantMappingMapper.toDto(merchantMapping);
    }

    @Override
    public void deleteMerchantMappingById(long id) {
        merchantMappingRepository.deleteById(id);
    }

    @Override
    public BigDecimal applyCashbackToAmount(@NotNull @Positive Long merchantId,
                                            @NotNull List<MerchantMapping> merchantMappings,
                                            @NotNull BigDecimal amount) {
        BigDecimal maxAmountWithCashback = BigDecimal.ZERO;
        for (MerchantMapping merchantMapping : merchantMappings) {

            if (merchantId.equals(merchantMapping.getMerchantId())) {
                BigDecimal percentage = merchantMapping.getPercentage();
                BigDecimal amountWithCashback = percentageCalculator.calculatePercentageNumber(amount, percentage);
                maxAmountWithCashback = maxAmountWithCashback.max(amountWithCashback);
            }
        }
        return maxAmountWithCashback;
    }
}
