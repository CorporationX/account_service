package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.ResponseRequestDto;
import faang.school.accountservice.model.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    @Mapping(target = "isOpen", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "statusDescription", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    Request toEntity(CreateRequestDto dto);

    ResponseRequestDto toDto(Request request);
}
