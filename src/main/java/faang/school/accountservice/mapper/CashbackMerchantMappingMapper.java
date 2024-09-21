package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.CashbackMerchantMappingDto;
import faang.school.accountservice.entity.CashbackMerchantMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CashbackMerchantMappingMapper {
    @Mapping(source = "cashbackTariff.id", target = "cashbackTariffId")
    CashbackMerchantMappingDto toDto(CashbackMerchantMapping mapping);

    CashbackMerchantMapping toEntity(CashbackMerchantMappingDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CashbackMerchantMappingDto cashbackMerchantMappingDto, @MappingTarget CashbackMerchantMapping cashbackMerchantMapping);
}