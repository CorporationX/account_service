package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.request.CreateRequestDto;
import faang.school.accountservice.dto.request.ResponseRequestDto;
import faang.school.accountservice.entity.request.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(source = "status", target = "requestStatus")
    ResponseRequestDto toResponseRequestDto(Request request);

    @Mapping(target = "idempotencyToken", ignore = true)
    @Mapping(target = "isOpen", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "statusDetails", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "lockValue", ignore = true)
    Request toEntity(CreateRequestDto createRequestDto);
}
