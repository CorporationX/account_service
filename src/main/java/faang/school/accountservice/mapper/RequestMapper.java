package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.RequestDto;
import faang.school.accountservice.dto.UpdateStatusDto;
import faang.school.accountservice.model.Request;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    Request toEntity(CreateRequestDto createRequestDto);

    RequestDto toDto(Request request);
}
