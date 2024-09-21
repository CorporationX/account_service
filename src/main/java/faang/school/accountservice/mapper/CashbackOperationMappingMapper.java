package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.CashbackOperationMappingDto;
import faang.school.accountservice.entity.CashbackOperationMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CashbackOperationMappingMapper {

    @Mapping(source = "cashbackTariff.id", target = "cashbackTariffId")
    CashbackOperationMappingDto toDto(CashbackOperationMapping cashbackOperationMapping);

    CashbackOperationMapping toEntity(CashbackOperationMappingDto cashbackOperationMapping);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CashbackOperationMappingDto cashbackOperationMappingDto, @MappingTarget CashbackOperationMapping cashbackOperationMapping);
}
