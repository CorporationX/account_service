package faang.school.accountservice.service;

import faang.school.accountservice.dto.MerchantMappingDto;
import faang.school.accountservice.dto.MerchantMappingUpdateDto;
import faang.school.accountservice.dto.OperationTypeMappingDto;
import faang.school.accountservice.entity.MerchantMapping;

import java.math.BigDecimal;
import java.util.List;

public interface MerchantMappingService {

    MerchantMappingDto createMerchantMapping(MerchantMappingDto dto);

    MerchantMappingDto getMerchantMappingById(long id);

    MerchantMappingDto updateMerchantMapping(MerchantMappingUpdateDto dto);

    void deleteMerchantMappingById(long id);

    BigDecimal applyCashbackToAmount(Long merchantId, List<MerchantMapping> merchantMappings, BigDecimal amount);
}
