package faang.school.accountservice.mapper;

import faang.school.accountservice.dto.CreateRequestDto;
import faang.school.accountservice.dto.RequestEventDto;
import faang.school.accountservice.entity.Request;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {
    Request toEntity(CreateRequestDto createRequestDto);

    @Mapping(target = "id", source = "token")
    RequestEventDto toRequestEventDto(Request request);
}
