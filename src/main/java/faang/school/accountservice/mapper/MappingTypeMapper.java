package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.MappingTypeDto;
import faang.school.accountservice.entity.type.Merchant;
import faang.school.accountservice.entity.type.OperationType;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MappingTypeMapper {
    MappingTypeDto toDto(Merchant merchant);

    MappingTypeDto toDto(OperationType operationType);
}
