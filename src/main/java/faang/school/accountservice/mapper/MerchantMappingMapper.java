package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.MerchantMappingDto;
import faang.school.accountservice.dto.MerchantMappingUpdateDto;
import faang.school.accountservice.entity.MerchantMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MerchantMappingMapper {

    MerchantMapping toEntity(MerchantMappingDto dto);

    MerchantMappingDto toDto(MerchantMapping entity);

    @Named("toEntityList")
    List<MerchantMapping> toEntityList(List<MerchantMappingDto> dtos);

    @Named("toDtoList")
    List<MerchantMappingDto> toDtoList(List<MerchantMapping> entities);

    void updateMerchantMapping(MerchantMappingUpdateDto dto, @MappingTarget MerchantMapping entity);
}

