package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.Request.RequestDto;
import faang.school.accountservice.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "inputData", source = "inputData")
    RequestDto toDto(Request request);

    @Mapping(target = "inputData", source = "inputData")
    @Mapping(target = "idempotencyToken", ignore = true)
    Request toEntity(RequestDto requestDto);
}
