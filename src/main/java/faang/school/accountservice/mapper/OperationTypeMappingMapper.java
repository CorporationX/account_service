package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.OperationTypeMappingDto;
import faang.school.accountservice.dto.OperationTypeMappingUpdateDto;
import faang.school.accountservice.entity.OperationTypeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationTypeMappingMapper {

    @Named("toEntityList")
    OperationTypeMapping toEntity(OperationTypeMappingDto dto);

    @Named("toDtoList")
    OperationTypeMappingDto toDto(OperationTypeMapping entity);

    List<OperationTypeMapping> toEntityList(List<OperationTypeMappingDto> dtos);

    List<OperationTypeMappingDto> toDtoList(List<OperationTypeMapping> entities);

    void updateOperationTypeMapping(OperationTypeMappingUpdateDto dto, @MappingTarget OperationTypeMapping entity);
}

