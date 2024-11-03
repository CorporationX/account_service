package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.CashbackTariffDto;
import faang.school.accountservice.entity.CashbackTariff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {OperationTypeMappingMapper.class, MerchantMappingMapper.class})
public interface CashbackTariffMapper {

    @Mapping(target = "operationTypeMappings", source = "operationTypeMappingDtos", qualifiedByName = "toEntityList")
    @Mapping(target = "merchantMappings", source = "merchantMappingDtos", qualifiedByName = "toEntityList")
    CashbackTariff toEntity(CashbackTariffDto cashbackTariffDto);


    @Mapping(target = "operationTypeMappingDtos", source = "operationTypeMappings", qualifiedByName = "toDtoList")
    @Mapping(target = "merchantMappingDtos", source = "merchantMappings", qualifiedByName = "toDtoList")
    CashbackTariffDto toDto(CashbackTariff cashbackTariff);
}
