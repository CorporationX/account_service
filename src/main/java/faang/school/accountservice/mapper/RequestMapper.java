package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "requestInput", ignore = true)
    @Mapping(target = "isOpen", ignore = true)
    RequestDto toDto(Request request);

    @Mapping(target = "inputData", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "isOpen", ignore = true)
    Request toEntity(RequestDto requestDto);
}
