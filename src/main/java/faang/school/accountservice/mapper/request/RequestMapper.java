package faang.school.accountservice.mapper.request;

import faang.school.accountservice.dto.request.RequestCreateDto;
import faang.school.accountservice.dto.request.RequestResponseDto;
import faang.school.accountservice.entity.request.Request;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {

    RequestResponseDto toDto(Request entity);
    Request toEntity(RequestCreateDto dto);
}
